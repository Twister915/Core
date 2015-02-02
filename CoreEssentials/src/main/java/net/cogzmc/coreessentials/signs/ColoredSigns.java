package net.cogzmc.coreessentials.signs;

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
        if (!event.getPlayer().hasPermission("core.coloredsigns")) return;
        for (int x = 0, l = event.getLines().length; x < l; x++) {
            event.setLine(x, ChatColor.translateAlternateColorCodes('&', event.getLine(x)));
        }
    }
}
