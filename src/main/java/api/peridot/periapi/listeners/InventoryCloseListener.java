package api.peridot.periapi.listeners;

import api.peridot.periapi.PeriAPI;
import api.peridot.periapi.inventories.CustomInventory;
import api.peridot.periapi.inventories.PersonalInventoryData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

public class InventoryCloseListener implements Listener {

    private final Plugin plugin;
    private final PeriAPI periApi;

    public InventoryCloseListener(Plugin plugin, PeriAPI periApi) {
        this.plugin = plugin;
        this.periApi = periApi;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        if (this.periApi.getInventoryManager().getInventories().isEmpty()) return;
        for (CustomInventory customInventory : this.periApi.getInventoryManager().getInventories()) {
            if (event.getInventory().getHolder() == null) continue;
            if (!(event.getInventory().getHolder().equals(customInventory))) continue;
            customInventory.getCloseConsumer().accept(event);
            if (customInventory.isCloseable()) {
                PersonalInventoryData data = customInventory.getPersonalInventoryData(player);
                data.setOpenedPage(-1);
                data.setUpdateTask(null);
                return;
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> customInventory.open(player), 2);
            return;
        }
    }

}
