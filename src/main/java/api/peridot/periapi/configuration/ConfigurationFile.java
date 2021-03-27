package api.peridot.periapi.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigurationFile extends ConfigurationProvider {

    private final Plugin plugin;
    private final String fileName;
    private final String sectionName;
    private YamlConfiguration yamlConfiguration;

    public ConfigurationFile(Plugin plugin, String fileName, String sectionName) {
        super(plugin);

        this.plugin = plugin;
        this.fileName = fileName;
        this.sectionName = sectionName;

        reloadConfiguration();
        setSection(yamlConfiguration.getConfigurationSection(sectionName));
    }

    public ConfigurationFile(Plugin plugin, String fileName) {
        this(plugin, fileName, fileName);
    }

    public void reloadConfiguration() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            plugin.saveResource(fileName + ".yml", true);
        }

        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        setSection(yamlConfiguration.getConfigurationSection(sectionName));

        reload();
    }

    public String getDirectory() {
        return plugin.getDataFolder() + "/" + getFileName() + ".yml";
    }

    public String getFileName() {
        return fileName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }

}
