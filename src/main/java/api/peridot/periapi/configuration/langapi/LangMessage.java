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
            this.useChat = section.getBoolean("chat.enabled");
            this.useChatIfNotPlayer = section.getBoolean("chat.send-if-not-player");
            if (this.useChat || this.useChatIfNotPlayer) {
                if (section.isString("chat.content")) {
                    this.chatContent.add(ColorUtil.color(section.getString("chat.content")));
                } else if (section.isList("chat.content")) {
                    this.chatContent = ColorUtil.color(section.getStringList("chat.content"));
                }
            }

            this.useTitle = section.getBoolean("title.enabled");
            if (this.useTitle) {
                this.titleContent = ColorUtil.color(section.getString("title.content"));
                this.subtitleContent = ColorUtil.color(section.getString("title.sub-content"));
                this.fadeIn = section.getInt("title.fade-in");
                this.stay = section.getInt("title.stay");
                this.fadeOut = section.getInt("title.fade-out");
            }

            this.useActionBar = section.getBoolean("actionbar.enabled");
            if (this.useActionBar) {
                this.actionBarContent = ColorUtil.color(section.getString("actionbar.content"));
            }
        } catch (Exception ignored) {
        }
    }

    /* Sending */
    public void broadcast(Replacement... replacements) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.send(player, replacements);
        }
    }

    public void send(CommandSender sender, Replacement... replacements) {
        if (this.useChat || (!(sender instanceof Player) && this.useChatIfNotPlayer)) {
            this.getChatContent(replacements).forEach(sender::sendMessage);
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (this.useTitle) {
                String titleMsg = this.getTitleContent(replacements);
                String subtitleMsg = this.getSubtitleContent(replacements);
                NotificationPackets.sendTitle(player, titleMsg, subtitleMsg, this.fadeIn, this.stay, this.fadeOut);
            }
            if (this.useActionBar) {
                String actionBarMsg = this.getActionBarContent(replacements);
                NotificationPackets.sendActionbar(player, actionBarMsg);
            }
        }
    }

    /* Chat */
    public boolean useChat() {
        return this.useChat;
    }

    public boolean useChatIfNotPlayer() {
        return this.useChatIfNotPlayer;
    }

    public List<String> getChatContent(Replacement... replacements) {
        return this.chatContent.stream().map(s -> ReplacementUtil.replace(s, replacements)).collect(Collectors.toList());
    }

    /* Title */
    public boolean useTitle() {
        return this.useTitle;
    }

    public String getTitleContent(Replacement... replacements) {
        return ReplacementUtil.replace(this.titleContent, replacements);
    }

    public String getSubtitleContent(Replacement... replacements) {
        return ReplacementUtil.replace(this.subtitleContent, replacements);
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public int getStay() {
        return this.stay;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }

    /* Actionbar */
    public boolean useActionBar() {
        return this.useActionBar;
    }

    public String getActionBarContent(Replacement... replacements) {
        return ReplacementUtil.replace(this.actionBarContent, replacements);
    }

}
