package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.items.InventoryItem;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryContent {

    private final int rows;
    private final int columns;
    private final int size;
    private final Map<Integer, InventoryItem> inventoryItems = new ConcurrentHashMap<>();

    public InventoryContent(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.size = rows * columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getSize() {
        return size;
    }

    public Collection<InventoryItem> getItems() {
        return inventoryItems.values();
    }

    public Map<Integer, InventoryItem> getInventoryItemsMap() {
        return new HashMap<>(inventoryItems);
    }

    public InventoryItem getItem(int row, int column) {
        return inventoryItems.get(slotFromRowAndColumn(row, column));
    }

    public InventoryItem getItem(int slot) {
        return inventoryItems.get(slot);
    }

    public void setItem(int row, int column, InventoryItem inventoryItem) {
        Validate.isTrue(row >= 1, "Row value must be bigger or equal 1");
        Validate.isTrue(row <= getRows(), "Row value must be smaller or equal " + getRows());
        Validate.isTrue(column >= 1, "Column value must be bigger or equal 1");
        Validate.isTrue(column <= getColumns(), "Column value must be smaller or equal " + getColumns());

        this.setItem(slotFromRowAndColumn(row, column), inventoryItem);
    }

    public void setItem(int slot, InventoryItem inventoryItem) {
        Validate.notNull(inventoryItem, "Item cannot be null");
        Validate.isTrue(slot >= 0, "Slot must be bigger or equal 0");
        Validate.isTrue(slot <= getSize() - 1, "Slot must be smaller or equal " + (getSize() - 1));
        inventoryItems.put(slot, inventoryItem);
    }

    public void addItem(InventoryItem inventoryItem) {
        int slot = firstEmptySlot();
        if (slot == -1) return;
        this.setItem(slot, inventoryItem);
    }

    public void fill(InventoryItem inventoryItem) {
        for (int i = 0; i < getSize(); i++) {
            setItem(i, inventoryItem);
        }
    }

    public void fillEmpty(InventoryItem inventoryItem) {
        for (int i : emptySlots()) {
            setItem(i, inventoryItem);
        }
    }

    public void fillRow(int row, InventoryItem inventoryItem) {
        Validate.isTrue(row >= 1, "Row value must be bigger or equal 1");
        Validate.isTrue(row <= getRows(), "Row value must be smaller or equal " + getRows());

        for (int i = 0; i < getColumns(); i++) {
            setItem((row - 1) * getColumns() + i, inventoryItem);
        }
    }

    public void fillRowEmpty(int row, InventoryItem inventoryItem) {
        Validate.isTrue(row >= 1, "Row value must be bigger or equal 1");
        Validate.isTrue(row <= getRows(), "Row value must be smaller or equal " + getRows());

        for (int i = 0; i < getColumns(); i++) {
            int slot = (row - 1) * getColumns() + i;
            if (!isEmptySlot(slot)) continue;
            setItem(slot, inventoryItem);
        }
    }

    public void fillColumn(int column, InventoryItem inventoryItem) {
        Validate.isTrue(column >= 1, "Column value must be bigger or equal 1");
        Validate.isTrue(column <= getColumns(), "Column value must be smaller or equal " + getColumns());

        for (int i = 0; i < getRows(); i++) {
            setItem((column + (i * 9)) - 1, inventoryItem);
        }
    }

    public void fillColumnEmpty(int column, InventoryItem inventoryItem) {
        Validate.isTrue(column >= 1, "Column value must be bigger or equal 1");
        Validate.isTrue(column <= 9, "Column value must be smaller or equal " + getColumns());

        for (int i = 0; i < getRows(); i++) {
            int slot = (column + (i * getColumns())) - 1;
            if (!isEmptySlot(slot)) continue;
            setItem(slot, inventoryItem);
        }
    }

    public int slotFromRowAndColumn(int row, int column) {
        Validate.isTrue(row >= 1, "Row value must be bigger or equal 1");
        Validate.isTrue(row <= getRows(), "Row value must be smaller or equal " + getRows());
        Validate.isTrue(column >= 1, "Column value must be bigger or equal 1");
        Validate.isTrue(column <= getColumns(), "Column value must be smaller or equal " + getColumns());

        int slot = 0;

        slot += (row - 1) * getColumns();
        slot += column;

        return slot - 1;
    }

    public boolean isEmpty() {
        return inventoryItems.isEmpty();
    }

    public boolean isEmptySlot(int slot) {
        return inventoryItems.get(slot) == null
                || inventoryItems.get(slot).getItem() == null
                || inventoryItems.get(slot).getItem().getType() == null
                || inventoryItems.get(slot).getItem().getType() == Material.AIR;
    }

    public List<Integer> emptySlots() {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < getSize(); i++) {
            if (isEmptySlot(i)) list.add(i);
        }

        return list;
    }

    public int firstEmptySlot() {
        int slot = -1;

        for (int i = 0; i < getSize(); i++) {
            if (isEmptySlot(i)) return i;
        }

        return slot;
    }

    public void fillInventory(Inventory inventory) {
        inventory.clear();

        inventoryItems.forEach((slot, item) -> {
            if (slot + 1 <= inventory.getSize()) {
                if (item != null) {
                    inventory.setItem(slot, item.getItem());
                } else {
                    inventory.setItem(slot, new ItemStack(Material.AIR));
                }
            }
        });
    }

    public void clear() {
        inventoryItems.clear();
    }

    public SlotIterator iterator(List<InventoryItem> inventoryItems) {
        return new SlotIterator(inventoryItems);
    }

    public SlotIterator iterator(InventoryItem... inventoryItems) {
        return iterator(Arrays.asList(inventoryItems));
    }

    public class SlotIterator {

        private final List<InventoryItem> items;

        private int slotFrom = 0;
        private int slotTo = 0;
        private boolean onlyEmpty = false;
        private boolean loop = false;

        private SlotIterator(List<InventoryItem> items) {
            this.items = items;
        }

        public SlotIterator slotFrom(int slot) {
            Validate.isTrue(slot >= 0, "Start slot must be bigger or equal 0");

            this.slotFrom = slot;
            return this;
        }

        public SlotIterator slotTo(int slot) {
            Validate.isTrue(slot <= getSize() - 1, "End slot must be smaller or equal " + (getSize() - 1));

            this.slotTo = slot;
            return this;
        }

        public SlotIterator slotFrom(int row, int column) {
            Validate.isTrue(row >= 1, "Start row value must be bigger or equal 1");
            Validate.isTrue(row <= getRows(), "Start row value must be smaller or equal " + getRows());
            Validate.isTrue(column >= 1, "Start column value must be bigger or equal 1");
            Validate.isTrue(column <= getColumns(), "Start column value must be smaller or equal " + getColumns());

            this.slotFrom = slotFromRowAndColumn(row, column);
            return this;
        }

        public SlotIterator slotTo(int row, int column) {
            Validate.isTrue(row >= 1, "Start row value must be bigger or equal 1");
            Validate.isTrue(row <= getRows(), "Start row value must be smaller or equal " + getRows());
            Validate.isTrue(column >= 1, "Start column value must be bigger or equal 1");
            Validate.isTrue(column <= getColumns(), "Start column value must be smaller or equal " + getColumns());

            this.slotTo = slotFromRowAndColumn(row, column);
            return this;
        }

        public SlotIterator onlyEmpty(boolean onlyEmpty) {
            this.onlyEmpty = onlyEmpty;
            return this;
        }

        public SlotIterator loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public void iterate() {
            int i = 0;

            int from = Math.min(slotFrom, slotTo);
            int to = Math.max(slotFrom, slotTo);

            for (int slot = from; slot < to + 1; slot++) {
                if (!loop && i + 1 > items.size()) return;
                if (loop && i + 1 > items.size()) {
                    i = (i % items.size()) - 1;
                    if (i < 0) i = 0;
                }
                if (onlyEmpty && !isEmptySlot(slot)) continue;
                setItem(slot, items.get(i));
                i++;
            }
        }

    }

}
