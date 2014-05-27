package net.cogzmc.hub.modules;

import lombok.Data;
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
@Data
public final class NoBuild implements Listener {
    private boolean noBuild;

    {
        noBuild = Hub.getInstance().getConfig().getBoolean("no-build", true);
    }

    @EventHandler
    public final void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission("hub.build") && noBuild) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasPermission("hub.build") && noBuild) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!event.getPlayer().hasPermission("hub.drop")) {
            event.setCancelled(true);
        }
    }
}
