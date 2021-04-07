package api.peridot.periapi.items;

import api.peridot.periapi.utils.replacements.Replacement;
import api.peridot.periapi.utils.replacements.ReplacementUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    @Deprecated
    public ItemBuilder(Material material, int amount, short durability) {
        this(new ItemStack(material, amount, durability));
    }

    public ItemBuilder(ItemStack itemStack) {
        this(itemStack, itemStack.getItemMeta());
    }

    private ItemBuilder(ItemStack itemStack, ItemMeta itemMeta) {
        this.itemStack = itemStack;
        this.itemMeta = itemMeta;
    }

    @Override
    public ItemBuilder clone() {
        if (this.itemStack == null || this.itemStack.getType() == Material.AIR) {
            new ItemBuilder(Material.AIR);
        }
        return new ItemBuilder(this.itemStack.clone(), this.itemMeta.clone());
    }

    public ItemBuilder setDurability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder replaceInName(Replacement... replacements) {
        this.itemMeta.setDisplayName(ReplacementUtil.replace(this.itemMeta.getDisplayName(), replacements));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    public ItemBuilder addLoreLine(String line) {
        List<String> lore = this.itemMeta.hasLore() ? new ArrayList<>(this.itemMeta.getLore()) : new ArrayList<>();
        lore.add(line);
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addLoreLine(String line, int lineIndex) {
        List<String> lore = this.itemMeta.hasLore() ? new ArrayList<>(this.itemMeta.getLore()) : new ArrayList<>();
        lore.set(lineIndex, line);
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        List<String> lore = this.itemMeta.hasLore() ? new ArrayList<>(this.itemMeta.getLore()) : new ArrayList<>();
        if (!lore.contains(line)) return this;
        lore.remove(line);
        return this;
    }

    public ItemBuilder removeLoreLine(int lineIndex) {
        List<String> lore = this.itemMeta.hasLore() ? new ArrayList<>(this.itemMeta.getLore()) : new ArrayList<>();
        if (lineIndex < 0 || lineIndex > lore.size()) return this;
        lore.remove(lineIndex);
        return this;
    }

    public ItemBuilder replaceInLore(Replacement... replacements) {
        List<String> lore = this.itemMeta.hasLore() ? new ArrayList<>(this.itemMeta.getLore()) : new ArrayList<>();
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, ReplacementUtil.replace(lore.get(i), replacements));
        }
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        Validate.isTrue(level >= 1, "Enchantment level must be equal or greater 1");
        this.itemMeta.addEnchant(enchantment, level, false);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        Validate.isTrue(level >= 1, "Enchantment level must be equal or greater 1");
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addUnsafeEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addUnsafeEnchantment);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        this.itemMeta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder addBookEnchantment(Enchantment enchantment, int level) {
        Validate.isTrue(level >= 1, "Enchantment level must be equal or greater 1");
        try {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) this.itemMeta;
            enchantmentStorageMeta.addStoredEnchant(enchantment, level, false);
            this.itemMeta = enchantmentStorageMeta;
        } catch (Exception ignored) {
        }
        return this;
    }

    public ItemBuilder addBookEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addBookEnchantment);
        return this;
    }

    public ItemBuilder addUnsafeBookEnchantment(Enchantment enchantment, int level) {
        Validate.isTrue(level >= 1, "Enchantment level must be equal or greater 1");
        try {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) this.itemMeta;
            enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
            this.itemMeta = enchantmentStorageMeta;
        } catch (Exception ignored) {
        }
        return this;
    }

    public ItemBuilder addUnsafeBookEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addUnsafeBookEnchantment);
        return this;
    }

    public ItemBuilder removeBookEnchantment(Enchantment enchantment) {
        try {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) this.itemMeta;
            enchantmentStorageMeta.removeStoredEnchant(enchantment);
            this.itemMeta = enchantmentStorageMeta;
        } catch (Exception ignored) {
        }
        return this;
    }

    public ItemBuilder setSkullOwner(String skullOwner) {
        if (skullOwner != null && !skullOwner.isEmpty()) {
            try {
                SkullMeta skullMeta = (SkullMeta) this.itemMeta;
                skullMeta.setOwner(skullOwner);
                this.itemMeta = skullMeta;
            } catch (Exception ignored) {
            }
        }
        return this;
    }

    public ItemBuilder setCustomSkullOwner(String url) {
        if (url != null && !url.isEmpty()) {
            try {
                SkullMeta skullMeta = (SkullMeta) this.itemMeta;

                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", url));
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);

                this.itemMeta = skullMeta;
            } catch (Exception ignored) {
            }
        }
        return this;
    }

    @Deprecated
    public ItemBuilder setDyeColor(DyeColor color) {
        this.itemStack.setDurability(color.getDyeData());
        return this;
    }

    @Deprecated
    public ItemBuilder setWoolColor(DyeColor color) {
        if (this.itemStack.getType().name().contains("WOOL")) return this;
        this.itemStack.setDurability(color.getWoolData());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemStack.getItemMeta();
            leatherArmorMeta.setColor(color);
            this.itemMeta = leatherArmorMeta;
        } catch (Exception ignored) {
        }
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

}
