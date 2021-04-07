package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.items.InventoryItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pagination {

    private List<InventoryItem> items = new ArrayList<>();
    private int itemsPerPage = 1;

    public List<InventoryItem> getItems() {
        return new ArrayList<>(this.items);
    }

    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }

    public void setItems(InventoryItem... items) {
        this.setItems(Arrays.asList(items));
    }

    public int getItemsPerPage() {
        return this.itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public List<InventoryItem> getItemsForPage(int page) {
        int indexTo = (page + 1) * this.itemsPerPage;
        if (indexTo > this.items.size()) indexTo = this.items.size();

        return this.items.subList(page * this.itemsPerPage, indexTo);
    }

    public int getPageCount() {
        return (int) Math.ceil((float) this.items.size() / this.itemsPerPage);
    }

    public boolean isFirst(int page) {
        return page <= 0;
    }

    public boolean isLast(int page) {
        return page >= this.getPageCount() - 1;
    }

}
