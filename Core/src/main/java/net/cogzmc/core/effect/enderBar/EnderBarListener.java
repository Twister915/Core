package net.cogzmc.core.effect.enderBar;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
        if (enderBarFor != null) enderBarFor.newWorld();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        CPlayer player = Core.getOnlinePlayer(event.getPlayer());
        final EnderBar enderBarFor = manager.getEnderBarFor(player);
        if (enderBarFor != null) Bukkit.getScheduler().runTask(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (enderBarFor.isSpawned()) enderBarFor.respawn();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        CPlayer player = Core.getOnlinePlayer(event.getPlayer());
        EnderBar enderBar = manager.getEnderBarFor(player);
        if (enderBar != null && enderBar.isSpawned()) enderBar.updateLocation();
    }
}
