package api.peridot.periapi.utils.simple;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtil {

    private ColorUtil() {
    }

    public static String color(String string) {
        if (string == null || string.isEmpty()) return "";
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> color(List<String> list) {
        if (list == null || list.isEmpty()) return Arrays.asList("");
        return list.stream()
                .map(ColorUtil::color).collect(Collectors.toList());
    }

    public static List<String> color(String... strings) {
        return color(Arrays.asList(strings));
    }

}
