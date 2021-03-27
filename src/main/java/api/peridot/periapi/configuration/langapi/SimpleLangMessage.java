package api.peridot.periapi.configuration.langapi;

import api.peridot.periapi.utils.replacements.Replacement;
import api.peridot.periapi.utils.replacements.ReplacementUtil;
import api.peridot.periapi.utils.simple.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleLangMessage {

    private boolean useChat = false;
    private List<String> chatContent = new ArrayList<>();

    public SimpleLangMessage(ConfigurationSection section) {
        try {
            useChat = section.get("content") != null;
            if (section.isString("content")) {
                chatContent.add(ColorUtil.color(section.getString("content")));
            } else if (section.isList("content")) {
                chatContent = ColorUtil.color(section.getStringList("content"));
            }
        } catch (Exception ignored) {
        }
    }

    /* Sending */
    public void broadcast(Replacement... replacements) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, replacements);
        }
    }

    public void send(CommandSender sender, Replacement... replacements) {
        if (useChat) {
            getChatContent(replacements).forEach(sender::sendMessage);
        }
    }

    /* Chat */
    public boolean useChat() {
        return useChat;
    }

    public List<String> getChatContent(Replacement... replacements) {
        return chatContent.stream().map(s -> ReplacementUtil.replace(s, replacements)).collect(Collectors.toList());
    }

    /* Utils */
    public String getStringLine() {
        if (chatContent.size() == 1) return chatContent.get(0);
        return chatContent.toString();
    }

}
