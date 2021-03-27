package api.peridot.periapi.inventories.providers;

import api.peridot.periapi.inventories.InventoryContent;
import org.bukkit.entity.Player;

public interface InventoryProvider {

    void init(Player player, InventoryContent content);

    void update(Player player, InventoryContent content);

}
