package api.peridot.periapi.configuration.langapi;

import api.peridot.periapi.packets.NotificationPackets;
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

public class LangMessage {

    private boolean useChat = false;
    private boolean useChatIfNotPlayer = false;
    private List<String> chatContent = new ArrayList<>();

    private boolean useTitle = false;
    private String titleContent = "";
    private String subtitleContent = "";
    private int fadeIn = 0;
    private int stay = 0;
    private int fadeOut = 0;

    private boolean useActionBar = false;
    private String actionBarContent = "";

    public LangMessage(ConfigurationSection section) {
        try {
            useChat = section.getBoolean("chat.enabled");
            useChatIfNotPlayer = section.getBoolean("chat.send-if-not-player");
            if (useChat || useChatIfNotPlayer) {
                if (section.isString("chat.content")) {
                    chatContent.add(ColorUtil.color(section.getString("chat.content")));
                } else if (section.isList("chat.content")) {
                    chatContent = ColorUtil.color(section.getStringList("chat.content"));
                }
            }

            useTitle = section.getBoolean("title.enabled");
            if (useTitle) {
                titleContent = ColorUtil.color(section.getString("title.content"));
                subtitleContent = ColorUtil.color(section.getString("title.sub-content"));
                fadeIn = section.getInt("title.fade-in");
                stay = section.getInt("title.stay");
                fadeOut = section.getInt("title.fade-out");
            }

            useActionBar = section.getBoolean("actionbar.enabled");
            if (useActionBar) {
                actionBarContent = ColorUtil.color(section.getString("actionbar.content"));
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
        if (useChat || (!(sender instanceof Player) && useChatIfNotPlayer)) {
            getChatContent(replacements).forEach(sender::sendMessage);
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (useTitle) {
                String titleMsg = getTitleContent(replacements);
                String subtitleMsg = getSubtitleContent(replacements);
                NotificationPackets.sendTitle(player, titleMsg, subtitleMsg, fadeIn, stay, fadeOut);
            }
            if (useActionBar) {
                String actionBarMsg = getActionBarContent(replacements);
                NotificationPackets.sendActionbar(player, actionBarMsg);
            }
        }
    }

    /* Chat */
    public boolean useChat() {
        return useChat;
    }

    public boolean useChatIfNotPlayer() {
        return useChatIfNotPlayer;
    }

    public List<String> getChatContent(Replacement... replacements) {
        return chatContent.stream().map(s -> ReplacementUtil.replace(s, replacements)).collect(Collectors.toList());
    }

    /* Title */
    public boolean useTitle() {
        return useTitle;
    }

    public String getTitleContent(Replacement... replacements) {
        return ReplacementUtil.replace(titleContent, replacements);
    }

    public String getSubtitleContent(Replacement... replacements) {
        return ReplacementUtil.replace(subtitleContent, replacements);
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    /* Actionbar */
    public boolean useActionBar() {
        return useActionBar;
    }

    public String getActionBarContent(Replacement... replacements) {
        return ReplacementUtil.replace(actionBarContent, replacements);
    }

}
