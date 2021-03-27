package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.providers.InventoryProvider;
import api.peridot.periapi.packets.Reflection;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CustomInventory implements InventoryHolder {

    private final Plugin plugin;
    private final PeriInventoryManager manager;
    private final Map<UUID, PersonalInventoryData> personalInventoriesDataMap = new ConcurrentHashMap<>();
    private final InventoryType inventoryType;
    private final int rows;
    private final int size;
    private final InventoryProvider provider;
    private final int columns;
    private String title;
    private boolean closeable;
    private int updateDelay;
    private Consumer<InventoryCloseEvent> closeConsumer;
    private InventoryContent content;

    private CustomInventory(Plugin plugin, PeriInventoryManager manager, InventoryProvider provider, InventoryType inventoryType, int rows) {
        this.plugin = plugin;
        this.manager = manager;
        this.provider = provider;
        this.inventoryType = inventoryType;
        switch (inventoryType) {
            case ANVIL:
                rows = 1;
                this.columns = 3;
                break;
            case BEACON:
                rows = 1;
                this.columns = 1;
                break;
            case BREWING:
                rows = 1;
                if (Reflection.serverVersionNumber < 9) {
                    this.columns = 3;
                } else {
                    this.columns = 4;
                }
                break;
            case DISPENSER:
                rows = 3;
                this.columns = 3;
                break;
            case DROPPER:
                rows = 3;
                this.columns = 3;
                break;
            case ENCHANTING:
                rows = 1;
                this.columns = 2;
                break;
            case FURNACE:
                rows = 1;
                this.columns = 3;
                break;
            case HOPPER:
                rows = 1;
                this.columns = 5;
                break;
            case WORKBENCH:
                rows = 2;
                this.columns = 5;
                break;
            default:
                this.columns = 9;
                break;
        }
        this.rows = rows;
        this.content = new InventoryContent(rows, columns);
        this.size = rows * columns;
        manager.addInventory(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
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

    public boolean isCloseable() {
        return closeable;
    }

    public int getUpdateDelay() {
        return updateDelay;
    }

    public Consumer<InventoryCloseEvent> getCloseConsumer() {
        return closeConsumer;
    }

    public InventoryProvider getProvider() {
        return provider;
    }

    public InventoryContent getContent() {
        return content;
    }

    public PersonalInventoryData getPersonalInventoryData(Player player) {
        PersonalInventoryData inventoryData = personalInventoriesDataMap.get(player.getUniqueId());

        if (inventoryData == null) {
            inventoryData = new PersonalInventoryData();
            personalInventoriesDataMap.put(player.getUniqueId(), inventoryData);
        }

        return inventoryData;
    }

    @Override
    public Inventory getInventory() {
        if (inventoryType == null) {
            return Bukkit.createInventory(this, this.rows * 9, this.title);
        } else {
            return Bukkit.createInventory(this, this.inventoryType, this.title);
        }
    }

    public BukkitTask getUpdateTask(Player player) {
        PersonalInventoryData inventoryData = getPersonalInventoryData(player);
        BukkitTask updateTask = inventoryData.getUpdateTask();

        if (updateTask == null) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (inventory != null && inventory.getHolder().equals(this)) {
                updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
                    provider.update(player, content);
                    content.fillInventory(inventory);
                }, 0, this.updateDelay);
            }
            inventoryData.setUpdateTask(updateTask);
        }

        return updateTask;
    }

    public void open(Player player, int page) {
        Validate.isTrue(page >= 0, "Page value must be bigger or equal 0");
        getPersonalInventoryData(player).setOpenedPage(page);
        open(player);
    }

    public void open(Player player) {
        Inventory inventory = getInventory();

        provider.init(player, this.content);
        this.content.fillInventory(inventory);

        getUpdateTask(player);

        player.openInventory(inventory);
    }

    public void update(Player player, int page) {
        Validate.isTrue(page >= 0, "Page value must be bigger or equal 0");
        getPersonalInventoryData(player).setOpenedPage(page);
        update(player);
    }

    public void update(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory != null && inventory.getHolder().equals(this)) {
            provider.init(player, this.content);
            this.content.fillInventory(inventory);
        }
    }

    public static final class Builder {

        private String title = "";
        private InventoryType inventoryType;
        private int rows = 6;
        private boolean closeable = true;
        private int updateDelay = -1;

        private Consumer<InventoryCloseEvent> closeConsumer = event -> {
        };

        private Plugin plugin;
        private PeriInventoryManager manager;
        private InventoryProvider provider;
        private InventoryContent content;

        private Builder() {
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder inventoryType(InventoryType inventoryType) {
            this.inventoryType = inventoryType;
            return this;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        public Builder updateDelay(int delay) {
            this.updateDelay = delay;
            return this;
        }

        public Builder closeConsumer(Consumer<InventoryCloseEvent> closeConsumer) {
            this.closeConsumer = closeConsumer;
            return this;
        }

        public Builder plugin(Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder manager(PeriInventoryManager manager) {
            this.manager = manager;
            return this;
        }

        public Builder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder content(InventoryContent content) {
            this.content = content;
            return this;
        }

        public CustomInventory build() {
            if (this.plugin == null) {
                throw new IllegalStateException("The plugin instance is required");
            }

            if (this.manager == null) {
                throw new IllegalStateException("The manager of the CustomInventory.Builder is required");
            }

            if (this.provider == null) {
                throw new IllegalStateException("The provider of the CustomInventory.Builder is required");
            }

            CustomInventory inventory = new CustomInventory(this.plugin, this.manager, this.provider, this.inventoryType, this.rows);

            inventory.title = this.title;
            inventory.closeable = this.closeable;
            inventory.updateDelay = this.updateDelay;
            inventory.closeConsumer = this.closeConsumer;

            if (content != null) inventory.content = this.content;

            return inventory;
        }

    }

}