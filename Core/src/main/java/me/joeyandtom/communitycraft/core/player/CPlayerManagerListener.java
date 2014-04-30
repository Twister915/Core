package me.joeyandtom.communitycraft.core.player;

import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Data
public final class CPlayerManagerListener implements Listener {
    private final CPlayerManager playerManager;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerManager.playerLoggedIn(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerManager.playerLoggedOut(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        playerManager.playerLoggedOut(event.getPlayer());
    }
}
