package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.items.InventoryItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pagination {

    private List<InventoryItem> items = new ArrayList<>();
    private int itemsPerPage = 1;

    public List<InventoryItem> getItems() {
        return new ArrayList<>(items);
    }

    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }

    public void setItems(InventoryItem... items) {
        setItems(Arrays.asList(items));
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public List<InventoryItem> getItemsForPage(int page) {
        int indexTo = (page + 1) * itemsPerPage;
        if (indexTo > items.size()) indexTo = items.size();

        return items.subList(page * itemsPerPage, indexTo);
    }

    public int getPageCount() {
        return (int) Math.ceil((float) this.items.size() / itemsPerPage);
    }

    public boolean isFirst(int page) {
        return page <= 0;
    }

    public boolean isLast(int page) {
        return page >= getPageCount() - 1;
    }

}
