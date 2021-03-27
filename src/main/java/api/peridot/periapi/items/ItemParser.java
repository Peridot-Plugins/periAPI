package api.peridot.periapi.items;

import api.peridot.periapi.utils.simple.ColorUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemParser {

    private ItemParser() {
    }

    public static ItemBuilder parseItemBuilder(ConfigurationSection section) {
        if (section == null) return null;

        ItemBuilder item = new ItemBuilder(Material.matchMaterial(section.getString("material")), Math.max(section.getInt("amount"), 1));

        String name = ColorUtil.color(section.getString("name"));
        List<String> lore = ColorUtil.color(section.getStringList("lore"));

        if (!name.isEmpty()) item.setName(name);
        if (!lore.isEmpty() && !(lore.size() == 1 && lore.get(0).isEmpty())) item.setLore(lore);

        item.setDurability((short) section.getInt("durability"));
        item.addUnsafeEnchantments(parseEnchantments(section.getStringList("enchantments")));
        item.addUnsafeBookEnchantments(parseEnchantments(section.getStringList("book-enchantments")));
        item.setSkullOwner(section.getString("skull-owner"));
        item.setCustomSkullOwner(section.getString("skull-texture"));
        item.setLeatherArmorColor(Color.fromRGB(section.getInt("color.red"), section.getInt("color.green"), section.getInt("color.blue")));

        return item;
    }

    public static ItemStack parseItemStack(ConfigurationSection section) {
        return parseItemBuilder(section).build();
    }

    private static Map<Enchantment, Integer> parseEnchantments(List<String> enchantments) {
        Map<Enchantment, Integer> enchantmentsMap = new HashMap<>();
        for (String enchantmentString : enchantments) {
            try {
                String[] splitedEnchantment = enchantmentString.split(":", 2);

                Enchantment enchantment = Enchantment.getByName(splitedEnchantment[0]);
                int level = Integer.parseInt(splitedEnchantment[1]);

                enchantmentsMap.put(enchantment, level);
            } catch (Exception ex) {
            }
        }

        return enchantmentsMap;
    }

}
