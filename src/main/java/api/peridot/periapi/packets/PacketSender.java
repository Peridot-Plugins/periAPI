package api.peridot.periapi.packets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PacketSender {

    private static Class<?> PLAYER_CONNECTION_CLASS;
    private static Reflection.MethodInvoker SEND_PACKET;

    static {
        try {
            PLAYER_CONNECTION_CLASS = Reflection.getMinecraftClass("PlayerConnection");
            SEND_PACKET = Reflection.getMethod(PLAYER_CONNECTION_CLASS, "sendPacket", Reflection.getMinecraftClass("Packet"));
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not setup PacketSender");
            ex.printStackTrace();
        }
    }

    public static void sendPacket(Object... packets) {
        sendPacket(Arrays.asList(packets));
    }

    public static void sendPacket(List<Object> packets) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packets);
        }
    }

    public static void sendPacket(List<Player> players, Object... packets) {
        sendPacket(players, Arrays.asList(packets));
    }


    public static void sendPacket(List<Player> players, List<Object> packets) {
        if (players.isEmpty()) return;

        for (Player player : players) {
            sendPacket(player, packets);
        }
    }

    public static void sendPacket(Player player, Object... packets) {
        sendPacket(player, Arrays.asList(packets));
    }

    public static void sendPacket(Player player, List<Object> packets) {
        if (player == null) return;
        try {
            for (Object packet : packets) {
                SEND_PACKET.invoke(getConnection(player), packet);
            }
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not send packet to player");
            ex.printStackTrace();
        }
    }

    public static Object getHandle(Player player) {
        Object handle = null;
        try {
            Reflection.MethodInvoker getHandle = Reflection.getMethod(player.getClass(), "getHandle");
            handle = getHandle.invoke(player);
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not get player handle");
            ex.printStackTrace();
        }
        return handle;
    }

    public static Object getConnection(Player player) {
        Object connection = null;
        try {
            Object handle = getHandle(player);
            Reflection.FieldAccessor<?> connectionField = Reflection.getField(handle.getClass(), "playerConnection", PLAYER_CONNECTION_CLASS);
            connection = connectionField.get(handle);
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not get player connection");
            ex.printStackTrace();
        }
        return connection;
    }

}
