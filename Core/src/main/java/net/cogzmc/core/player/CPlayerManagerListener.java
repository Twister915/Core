package net.cogzmc.core.player;

import lombok.Data;
import net.cogzmc.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This will talk to any {@link net.cogzmc.core.player.CPlayerManager} and bridge communications between Bukkit and the Core.
 */
@Data
public final class CPlayerManagerListener implements Listener {
    private final CPlayerManager playerManager;

    //no docs
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerLoginEvent event) {
        if (event.getPlayer().getName().equalsIgnoreCase("gmlukens")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "B&");
            return;
        }
        for (CPlayer cPlayer : playerManager.getOnlinePlayers()) {
            if (cPlayer.getName().equals(event.getPlayer().getName())) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You are already on this server. Try logging in again!");
                return;
            }
        }
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return; //Prevent whitelist from causing memory leaks.
        Player player = event.getPlayer();
        try {
            playerManager.playerLoggedIn(player, event.getAddress());
        } catch (CPlayerJoinException e) {
            event.setKickMessage(e.getDisconectMessage());
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) playerManager.playerLoggedOut(event.getPlayer());
    }

    //no docs
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (playerManager.getCPlayerForPlayer(event.getPlayer()) != null) playerManager.playerLoggedOut(event.getPlayer());
    }

    //no docs
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        if (playerManager.getCPlayerForPlayer(event.getPlayer()) != null) playerManager.playerLoggedOut(event.getPlayer());
    }
}
