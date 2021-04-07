package api.peridot.periapi.configuration.langapi;

import api.peridot.periapi.utils.replacements.Replacement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class LangAPI {

    private final Logger logger;
    private final Map<String, LangMessage> messages = new ConcurrentHashMap<>();
    private final Map<String, SimpleLangMessage> simpleMessages = new ConcurrentHashMap<>();
    private ConfigurationSection section;

    public LangAPI(ConfigurationSection section) {
        this.section = section;
        this.logger = Bukkit.getLogger();
    }

    public void setSection(ConfigurationSection section) {
        this.section = section;
    }

    /* Getting or Creating messages */

    public LangMessage getMessage(String id) {
        return this.getMessage(id, false);
    }

    public LangMessage getMessage(String id, boolean force) {
        LangMessage message = this.messages.get(id);

        if (message == null || force) {
            message = new LangMessage(this.section.getConfigurationSection(id));
            this.messages.put(id, message);
        }

        return message;
    }

    public SimpleLangMessage getSimpleMessage(String id) {
        return this.getSimpleMessage(id, false);
    }

    public SimpleLangMessage getSimpleMessage(String id, boolean force) {
        SimpleLangMessage message = this.simpleMessages.get(id);

        if (message == null || force) {
            message = new SimpleLangMessage(this.section.getConfigurationSection(id));
            this.simpleMessages.put(id, message);
        }

        return message;
    }

    /* Sending */

    public void broadcast(String id, Replacement... replacements) {
        LangMessage message = this.getMessage(id);
        message.broadcast(replacements);
    }

    public void sendMessage(CommandSender sender, String id, Replacement... replacements) {
        LangMessage message = this.getMessage(id);
        message.send(sender, replacements);
    }

    public void broadcastSimple(String id, Replacement... replacements) {
        SimpleLangMessage message = this.getSimpleMessage(id);
        message.broadcast(replacements);
    }

    public void sendSimpleMessage(CommandSender sender, String id, Replacement... replacements) {
        SimpleLangMessage message = this.getSimpleMessage(id);
        message.send(sender, replacements);
    }

    /* Reload */

    public void reload() {
        if (this.section == null) {
            this.logger.warning("[LangAPI] Missing messages section!");
            return;
        }

        if (!this.messages.isEmpty()) {
            this.messages.keySet().forEach(id -> {
                this.messages.put(id, this.getMessage(id, true));
            });
        }

        if (!this.simpleMessages.isEmpty()) {
            this.simpleMessages.keySet().forEach(id -> {
                this.simpleMessages.put(id, this.getSimpleMessage(id, true));
            });
        }
    }

}
