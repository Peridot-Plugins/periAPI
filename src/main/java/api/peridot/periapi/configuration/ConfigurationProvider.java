package api.peridot.periapi.configuration;

import api.peridot.periapi.items.ItemBuilder;
import api.peridot.periapi.items.ItemParser;
import api.peridot.periapi.utils.replacements.Replacement;
import api.peridot.periapi.utils.replacements.ReplacementUtil;
import api.peridot.periapi.utils.simple.ColorUtil;
import api.peridot.periapi.utils.simple.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConfigurationProvider {

    private final Map<String, Object> valuesMap = new ConcurrentHashMap<>();

    private final Logger logger;

    private ConfigurationSection section;

    public ConfigurationProvider(Plugin plugin, ConfigurationSection section) {
        this.logger = plugin.getLogger();
        this.section = section;
    }

    @Deprecated
    public ConfigurationProvider(ConfigurationSection section) {
        this.logger = Bukkit.getLogger();
        this.section = section;
    }

    public ConfigurationProvider(Plugin plugin) {
        this.logger = plugin.getLogger();
    }

    public void setSection(ConfigurationSection section) {
        this.section = section;
    }

    public Object getObject(String path) {
        return this.getObject(path, false);
    }

    public Object getObject(String path, boolean force) {
        Object value = this.valuesMap.get(path);
        if (value == null || force) {
            value = this.section.get(path);
            if (value != null) {
                this.valuesMap.put(path, value);
            }
        }
        return value;
    }

    public String getString(String path, Replacement... replacements) {
        Object value = this.getObject(path);
        String string = value instanceof String ? value.toString() : null;
        if (string != null) {
            string = ReplacementUtil.replace(string, replacements);
        }
        return string;
    }

    public String getColoredString(String path, Replacement... replacements) {
        String value = ColorUtil.color(this.getString(path, replacements));
        if (value != null) {
            this.valuesMap.put(path, value);
        }
        return value;
    }

    public boolean getBoolean(String path) {
        Object value = this.getObject(path);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    public byte getByte(String path) {
        Object value = this.getObject(path);
        return value instanceof Byte ? NumberUtil.toByte(value) : null;
    }

    public short getShort(String path) {
        Object value = this.getObject(path);
        return value instanceof Short ? NumberUtil.toShort(value) : null;
    }

    public int getInt(String path) {
        Object value = this.getObject(path);
        return value instanceof Integer ? NumberUtil.toInt(value) : null;
    }

    public long getLong(String path) {
        Object value = this.getObject(path);
        return value instanceof Long ? NumberUtil.toLong(value) : null;
    }

    public float getFloat(String path) {
        Object value = this.getObject(path);
        return value instanceof Float ? NumberUtil.toFloat(value) : null;
    }

    public double getDouble(String path) {
        Object value = this.getObject(path);
        return value instanceof Double ? NumberUtil.toDouble(value) : null;
    }

    public List<?> getList(String path) {
        Object value = this.getObject(path);
        return (List<?>) ((value instanceof List) ? value : null);
    }

    public List<String> getStringList(String path, Replacement... replacements) {
        List<?> list = this.getList(path);
        if (list == null) {
            return new ArrayList<String>();
        }
        List<String> result = new ArrayList<String>();
        for (Object object : list) {
            if (object instanceof String || this.isPrimitiveWrapper(object)) {
                result.add(String.valueOf(object));
            }
        }
        return ReplacementUtil.replace(result, replacements);
    }

    public List<String> getColoredStringList(String path, Replacement... replacements) {
        List<String> list = ColorUtil.color(this.getStringList(path, replacements));
        if (list != null) {
            this.valuesMap.put(path, list);
        }
        return list;
    }

    public Color getColor(String path) {
        Object value = this.valuesMap.get(path);
        if (value == null) {
            ConfigurationSection colorSection = this.section.getConfigurationSection(path);
            int red = colorSection.getInt("red");
            int green = colorSection.getInt("green");
            int blue = colorSection.getInt("blue");
            value = Color.fromRGB(red, green, blue);
            this.valuesMap.put(path, value);
        }
        return value instanceof Color ? (Color) value : null;
    }

    public ItemStack getItemStack(String path) {
        Object value = this.getObject(path);
        return value instanceof ItemBuilder ? (ItemStack) value : null;
    }

    public ItemStack getItemStackFromBuilder(String path) {
        ItemBuilder value = this.getItemBuilder(path);
        if (value == null) {
            return null;
        }
        return value.build();
    }

    public ItemBuilder getItemBuilder(String path) {
        Object value = this.valuesMap.get(path);
        if (value == null) {
            try {
                value = ItemParser.parseItemBuilder(this.section.getConfigurationSection(path));
            } catch (Exception ex) {
                value = null;
            }
            if (value != null) {
                this.valuesMap.put(path, value);
            }
        }
        return (ItemBuilder) value;
    }

    protected boolean isPrimitiveWrapper(Object input) {
        return input instanceof Integer || input instanceof Boolean ||
                input instanceof Character || input instanceof Byte ||
                input instanceof Short || input instanceof Double ||
                input instanceof Long || input instanceof Float;
    }

    public void reload() {
        if (this.section == null) {
            this.logger.warning("[ConfigurationProvider] Missing configuration section!");
            return;
        }

        if (!this.valuesMap.isEmpty()) {
            this.valuesMap.keySet().forEach(path -> {
                try {
                    this.valuesMap.put(path, this.getObject(path, true));
                } catch (Exception ignored) {
                }
            });
        }
    }

}
