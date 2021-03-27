package api.peridot.periapi.packets;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationPackets {

    private static Class<?> CHAT_MESSAGE_TYPE_CLASS;
    private static Object CHAT_MESSAGE_TYPE;

    private static Enum<?> TITLE_ENUM;
    private static Enum<?> SUBTITLE_ENUM;
    private static Enum<?> TIMES_ENUM;

    private static Reflection.ConstructorInvoker PACKET_PLAY_OUT_TITLE_CONSTRUCTOR;
    private static Reflection.ConstructorInvoker PACKET_PLAY_OUT_CHAT_CONSTRUCTOR;

    static {
        try {
            Class<?> CHAT_BASE_COMPONENT_CLASS = Reflection.getMinecraftClass("IChatBaseComponent");

            if (Reflection.usePre12Methods) {
                CHAT_MESSAGE_TYPE_CLASS = null;
            } else {
                CHAT_MESSAGE_TYPE_CLASS = Reflection.getMinecraftClass("ChatMessageType");
                CHAT_MESSAGE_TYPE = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];
            }

            Class<?> ENUM_TITLE_ACTION_CLASS = Reflection.serverVersion.equals("v1_8_R1") ? Reflection.getMinecraftClass("EnumTitleAction") : Reflection.getMinecraftClass("PacketPlayOutTitle$EnumTitleAction");
            Reflection.MethodInvoker TILE_ACTION_ENUM_METHOD = Reflection.getMethod(ENUM_TITLE_ACTION_CLASS, "valueOf", String.class);
            TITLE_ENUM = (Enum) TILE_ACTION_ENUM_METHOD.invoke(ENUM_TITLE_ACTION_CLASS, "TITLE");
            SUBTITLE_ENUM = (Enum) TILE_ACTION_ENUM_METHOD.invoke(ENUM_TITLE_ACTION_CLASS, "SUBTITLE");
            TIMES_ENUM = (Enum) TILE_ACTION_ENUM_METHOD.invoke(ENUM_TITLE_ACTION_CLASS, "TIMES");

            PACKET_PLAY_OUT_TITLE_CONSTRUCTOR = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutTitle"), ENUM_TITLE_ACTION_CLASS, CHAT_BASE_COMPONENT_CLASS, int.class, int.class, int.class);
            if (CHAT_MESSAGE_TYPE_CLASS == null) {
                PACKET_PLAY_OUT_CHAT_CONSTRUCTOR = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutChat"), CHAT_BASE_COMPONENT_CLASS, byte.class);
            } else {
                PACKET_PLAY_OUT_CHAT_CONSTRUCTOR = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutChat"), CHAT_BASE_COMPONENT_CLASS, CHAT_MESSAGE_TYPE_CLASS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Object> createTitlePacket(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        List<Object> packets = new ArrayList<>();
        try {
            Object titlePacket = PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.invoke(TITLE_ENUM, CommonPackets.createBaseComponent(title), -1, -1, -1);
            Object subtitlePacket = PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.invoke(SUBTITLE_ENUM, CommonPackets.createBaseComponent(subTitle), -1, -1, -1);
            Object timesPacket = PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.invoke(TIMES_ENUM, null, fadeIn, stay, fadeOut);

            packets.addAll(Arrays.asList(titlePacket, subtitlePacket, timesPacket));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return packets;
    }

    public static Object createActionBarPacket(String text) {
        Object packet = null;
        try {
            if (CHAT_MESSAGE_TYPE_CLASS != null) {
                packet = PACKET_PLAY_OUT_CHAT_CONSTRUCTOR.invoke(CommonPackets.createBaseComponent(text), CHAT_MESSAGE_TYPE);
            } else {
                packet = PACKET_PLAY_OUT_CHAT_CONSTRUCTOR.invoke(CommonPackets.createBaseComponent(text), (byte) 2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return packet;
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        PacketSender.sendPacket(player, createTitlePacket(title, subtitle, fadeIn, stay, fadeOut));
    }

    public static void sendActionbar(Player player, String text) {
        PacketSender.sendPacket(player, createActionBarPacket(text));
    }

}
