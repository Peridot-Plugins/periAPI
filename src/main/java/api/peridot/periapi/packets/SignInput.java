package api.peridot.periapi.packets;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.BiFunction;

public class SignInput {

    private final Class<?> packetPlayInUpdateSign = Reflection.getMinecraftClass("PacketPlayInUpdateSign");
    private final Class<?> chatBaseComponentClass = Reflection.getMinecraftClass("IChatBaseComponent");
    private final Class<?> chatBaseComponentArrayClass = Reflection.getClass("[L" + this.chatBaseComponentClass.getName() + ";");
    private final Class<?> blockPositionClass = Reflection.getMinecraftClass("BlockPosition");
    private final Reflection.ConstructorInvoker packetPlayOutOpenSignEditor = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutOpenSignEditor"), this.blockPositionClass);
    private final Reflection.ConstructorInvoker blockPosition = Reflection.getConstructor(this.blockPositionClass, int.class, int.class, int.class);
    private final Reflection.MethodInvoker getText = Reflection.getMethod(this.chatBaseComponentClass, "getText");
    private final Reflection.MethodInvoker getPositionX = Reflection.getMethod(this.blockPositionClass, "getX");
    private final Reflection.MethodInvoker getPositionY = Reflection.getMethod(this.blockPositionClass, "getY");
    private final Reflection.MethodInvoker getPositionZ = Reflection.getMethod(this.blockPositionClass, "getZ");
    private final Reflection.FieldAccessor<?> signPosition = Reflection.getField(this.packetPlayInUpdateSign, "a", this.blockPositionClass);
    private final Reflection.FieldAccessor<?> signMessage = Reflection.getField(this.packetPlayInUpdateSign, "b", this.chatBaseComponentArrayClass);

    private final Plugin plugin;
    private final TinyProtocol protocol;

    private String[] text;
    private BiFunction<Player, String[], Response> completeFunction;

    private SignInput(Plugin plugin) {
        this.plugin = plugin;

        this.protocol = new TinyProtocol(plugin) {
            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                if (SignInput.this.signMessage.hasField(packet)) {
                    String[] result = new String[4];
                    Object[] casted = (Object[]) SignInput.this.signMessage.get(packet);
                    for (int i = 0; i < 4; i++) {
                        result[i] = SignInput.this.getText.invoke(casted[i]).toString();
                    }

                    Response response = SignInput.this.completeFunction.apply(sender, result);

                    boolean close = response.type == Response.Type.CLOSE;

                    Object blockPosition = SignInput.this.signPosition.get(packet);

                    int signX = (int) SignInput.this.getPositionX.invoke(blockPosition);
                    int signY = (int) SignInput.this.getPositionY.invoke(blockPosition);
                    int signZ = (int) SignInput.this.getPositionZ.invoke(blockPosition);

                    Location signLocation = new Location(sender.getWorld(), signX, signY, signZ);
                    Block block = signLocation.getWorld().getBlockAt(signLocation);

                    if (!close) {
                        if (response.type == Response.Type.TEXT) {
                            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> SignInput.this.open(sender, response.text), 2);
                        } else {
                            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> SignInput.this.open(sender), 2);
                        }
                    } else {
                        sender.sendBlockChange(signLocation, block.getType(), block.getData());
                        SignInput.this.protocol.close();
                    }
                }

                return super.onPacketInAsync(sender, channel, packet);
            }
        };
    }

    public void open(Player player) {
        this.open(player, this.text);
    }

    private void open(Player player, String[] customText) {
        Location location = player.getLocation();
        int y = 255;
        if (location.getBlockX() >= 128) {
            y = 1;
        }
        location.setY(y);

        player.sendBlockChange(location, Material.SIGN_POST, (byte) 0);
        player.sendSignChange(location, customText);

        try {
            Object openSign = this.packetPlayOutOpenSignEditor.invoke(this.blockPosition.invoke(location.getBlockX(), location.getBlockY(), location.getBlockZ()));

            PacketSender.sendPacket(player, openSign);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Response response() {
        return new Response();
    }

    public static final class Builder {

        private String[] text = new String[]{"", "", "", ""};
        private BiFunction<Player, String[], Response> completeFunction;

        private Plugin plugin;

        private Builder() {
        }

        public Builder text(String... text) {
            this.text = text;
            return this;
        }

        public Builder text(List<String> text) {
            String text1 = "";
            String text2 = "";
            String text3 = "";
            String text4 = "";
            try {
                text1 = text.get(0);
                text2 = text.get(1);
                text3 = text.get(2);
                text4 = text.get(3);
            } catch (Exception ignored) {
            }
            this.text = new String[]{
                    text1,
                    text2,
                    text3,
                    text4
            };
            return this;
        }

        public Builder completeFunction(BiFunction<Player, String[], Response> completeFunction) {
            this.completeFunction = completeFunction;
            return this;
        }

        public Builder plugin(Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public SignInput build() {
            if (this.plugin == null) {
                throw new IllegalStateException("The plugin instance is required");
            }

            SignInput signInput = new SignInput(this.plugin);

            signInput.text = this.text;
            signInput.completeFunction = this.completeFunction;

            return signInput;
        }

    }

    public static final class Response {

        private Type type;
        private String[] text;

        private Response() {
        }

        public Response close() {
            this.type = Type.CLOSE;
            return this;
        }

        public Response reopen() {
            this.type = Type.REOPEN;
            return this;
        }

        public Response text(String... text) {
            this.type = Type.TEXT;
            this.text = text;
            return this;
        }

        public Response text(List<String> text) {
            this.type = Type.TEXT;
            this.text = new String[]{
                    text.get(0),
                    text.get(1),
                    text.get(2),
                    text.get(3)
            };
            return this;
        }

        private enum Type {
            CLOSE,
            REOPEN,
            TEXT
        }

    }

}
