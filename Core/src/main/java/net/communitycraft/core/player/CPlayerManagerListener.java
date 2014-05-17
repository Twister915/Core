package net.communitycraft.core.player;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This will talk to any {@link net.communitycraft.core.player.CPlayerManager} and bridge communications between Bukkit and the Core.
 */
@Data
public final class CPlayerManagerListener implements Listener {
    private final CPlayerManager playerManager;

    //no docs
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerManager.playerLoggedIn(player, player.getAddress().getAddress());
    }

    //no docs
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (playerManager.getCPlayerForPlayer(event.getPlayer()) != null) playerManager.playerLoggedOut(event.getPlayer());
    }

    //no docs
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        playerManager.playerLoggedOut(event.getPlayer());
    }
}
