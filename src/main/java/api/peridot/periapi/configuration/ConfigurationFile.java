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

        this.reloadConfiguration();
        this.setSection(this.yamlConfiguration.getConfigurationSection(sectionName));
    }

    public ConfigurationFile(Plugin plugin, String fileName) {
        this(plugin, fileName, fileName);
    }

    public void reloadConfiguration() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }

        File file = new File(this.plugin.getDataFolder(), this.fileName + ".yml");

        if (!file.exists()) {
            this.plugin.saveResource(this.fileName + ".yml", true);
        }

        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        this.setSection(this.yamlConfiguration.getConfigurationSection(this.sectionName));

        this.reload();
    }

    public String getDirectory() {
        return this.plugin.getDataFolder() + "/" + this.getFileName() + ".yml";
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getSectionName() {
        return this.sectionName;
    }

    public YamlConfiguration getYamlConfiguration() {
        return this.yamlConfiguration;
    }

}
