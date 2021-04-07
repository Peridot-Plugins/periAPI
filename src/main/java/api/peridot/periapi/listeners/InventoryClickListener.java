package api.peridot.periapi.listeners;

import api.peridot.periapi.PeriAPI;
import api.peridot.periapi.inventories.CustomInventory;
import api.peridot.periapi.inventories.InventoryContent;
import api.peridot.periapi.inventories.items.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class InventoryClickListener implements Listener {

    private final Plugin plugin;
    private final PeriAPI periApi;

    public InventoryClickListener(Plugin plugin, PeriAPI periApi) {
        this.plugin = plugin;
        this.periApi = periApi;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        Player player = (Player) event.getWhoClicked();

        if (this.periApi.getInventoryManager().getInventories().isEmpty()) return;
        for (CustomInventory customInventory : this.periApi.getInventoryManager().getInventories()) {
            if (event.getClickedInventory().getHolder() == null) continue;
            if (!(event.getClickedInventory().getHolder().equals(customInventory))) continue;
            if (!event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) return;

            InventoryContent content = customInventory.getContent();

            if (content == null) return;
            if (content.isEmpty()) return;
            for (int slot : content.getInventoryItemsMap().keySet()) {
                InventoryItem inventoryItem = content.getItem(slot);
                if (inventoryItem == null) continue;

                ItemStack item = inventoryItem.getItem();
                if (item == null || item.getType() == Material.AIR) continue;
                if (slot != event.getSlot()) continue;

                inventoryItem.run(event);

                if (inventoryItem.isCancel()) event.setCancelled(true);
                if (inventoryItem.isUpdate()) customInventory.update(player);
                return;
            }
        }
    }

}
