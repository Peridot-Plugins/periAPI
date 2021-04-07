package api.peridot.periapi.inventories.items;

import api.peridot.periapi.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InventoryItem {

    private final ItemStack item;
    private final Consumer<InventoryClickEvent> consumer;
    private boolean cancel = true;
    private boolean update = false;

    private InventoryItem(ItemStack item, Consumer<InventoryClickEvent> consumer, boolean cancel, boolean update) {
        this.item = item;
        this.consumer = consumer;
        this.cancel = cancel;
        this.update = update;
    }

    public ItemStack getItem() {
        if (this.item == null) return new ItemStack(Material.AIR);
        return this.item;
    }

    public Consumer<InventoryClickEvent> getConsumer() {
        return this.consumer;
    }

    public void run(InventoryClickEvent event) {
        this.consumer.accept(event);
    }

    public boolean isCancel() {
        return this.cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isUpdate() {
        return this.update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ItemStack item;
        private Consumer<InventoryClickEvent> consumer = event -> {
        };
        private boolean cancel = true;
        private boolean update = false;

        private Builder() {
        }

        public Builder item(ItemStack item) {
            this.item = item;
            return this;
        }

        public Builder item(ItemBuilder item) {
            this.item = item.clone().build();
            return this;
        }

        public Builder consumer(Consumer<InventoryClickEvent> consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder cancel(boolean cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder update(boolean update) {
            this.update = update;
            return this;
        }

        public InventoryItem build() {
            return new InventoryItem(this.item, this.consumer, this.cancel, this.update);
        }

    }

}
