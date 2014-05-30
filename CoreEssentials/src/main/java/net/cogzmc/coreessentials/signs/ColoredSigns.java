package net.cogzmc.coreessentials.signs;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/28/2014
 */
public class ColoredSigns implements Listener {
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("gearz.coloredsigns")) return;
        for (int x = 0, l = event.getLines().length; x < l; x++) {
            event.setLine(x, ChatColor.translateAlternateColorCodes('&', event.getLine(x)));
        }
    }
}
