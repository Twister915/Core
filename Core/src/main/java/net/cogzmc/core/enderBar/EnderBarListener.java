package net.cogzmc.core.enderBar;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@Data
final class EnderBarListener implements Listener {
    private final EnderBarManager manager;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        EnderBar enderBarFor = manager.getEnderBarFor(onlinePlayer);
        if (enderBarFor != null && enderBarFor.isSpawned() && event.getTo().distanceSquared(enderBarFor.getCurrentLocation()) > 25) enderBarFor.updateLocation();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        CPlayer player = Core.getOnlinePlayer(event.getPlayer());
        EnderBar enderBarFor = manager.getEnderBarFor(player);
        enderBarFor.newWorld();
    }
}
