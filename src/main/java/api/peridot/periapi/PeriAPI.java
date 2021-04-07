package api.peridot.periapi;

import api.peridot.periapi.inventories.PeriInventoryManager;
import api.peridot.periapi.listeners.InventoryClickListener;
import api.peridot.periapi.listeners.InventoryCloseListener;
import api.peridot.periapi.listeners.InventoryDragListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PeriAPI {

    private final Plugin plugin;

    private PeriInventoryManager inventoryManager;

    public PeriAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryClickListener(this.plugin, this), this.plugin);
        pluginManager.registerEvents(new InventoryCloseListener(this.plugin, this), this.plugin);
        pluginManager.registerEvents(new InventoryDragListener(this.plugin, this), this.plugin);

        this.inventoryManager = new PeriInventoryManager(this.plugin, this);
    }

    public PeriInventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

}
