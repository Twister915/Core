package net.cogzmc.hub.modules;

import net.cogzmc.hub.Hub;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class NoBuild implements Listener {
    @EventHandler
    @SuppressWarnings("unused")
    public final void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission("hub.build") && Hub.getInstance().getConfig().getBoolean("no-build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public final void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasPermission("hub.build") && Hub.getInstance().getConfig().getBoolean("no-build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public final void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!event.getPlayer().hasPermission("hub.drop")) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }
}
