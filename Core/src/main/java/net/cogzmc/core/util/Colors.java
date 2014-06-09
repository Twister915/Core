package net.cogzmc.core.util;

/**
 * Created by Joe on 6/9/2014.
 */
import org.bukkit.ChatColor;

public class Colors {
    public static String colorify(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String decolorify(String input) {
        return input.replace(ChatColor.COLOR_CHAR, '&');
    }

    public static String stripSpecialCharacters(String input) {
        return input.replaceAll("[&" + ChatColor.COLOR_CHAR + "].", "");
    }
}

