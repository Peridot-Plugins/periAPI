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
        return getMessage(id, false);
    }

    public LangMessage getMessage(String id, boolean force) {
        LangMessage message = messages.get(id);

        if (message == null || force) {
            message = new LangMessage(section.getConfigurationSection(id));
            messages.put(id, message);
        }

        return message;
    }

    public SimpleLangMessage getSimpleMessage(String id) {
        return getSimpleMessage(id, false);
    }

    public SimpleLangMessage getSimpleMessage(String id, boolean force) {
        SimpleLangMessage message = simpleMessages.get(id);

        if (message == null || force) {
            message = new SimpleLangMessage(section.getConfigurationSection(id));
            simpleMessages.put(id, message);
        }

        return message;
    }

    /* Sending */

    public void broadcast(String id, Replacement... replacements) {
        LangMessage message = getMessage(id);
        message.broadcast(replacements);
    }

    public void sendMessage(CommandSender sender, String id, Replacement... replacements) {
        LangMessage message = getMessage(id);
        message.send(sender, replacements);
    }

    public void broadcastSimple(String id, Replacement... replacements) {
        SimpleLangMessage message = getSimpleMessage(id);
        message.broadcast(replacements);
    }

    public void sendSimpleMessage(CommandSender sender, String id, Replacement... replacements) {
        SimpleLangMessage message = getSimpleMessage(id);
        message.send(sender, replacements);
    }

    /* Reload */

    public void reload() {
        if (section == null) {
            logger.warning("[LangAPI] Missing messages section!");
            return;
        }

        if (!messages.isEmpty()) {
            messages.keySet().forEach(id -> {
                messages.put(id, getMessage(id, true));
            });
        }

        if (!simpleMessages.isEmpty()) {
            simpleMessages.keySet().forEach(id -> {
                simpleMessages.put(id, getSimpleMessage(id, true));
            });
        }
    }

}
