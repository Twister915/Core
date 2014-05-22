package net.cogzmc.hub.modules;

import net.cogzmc.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class HideStream implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public final void onJoin(PlayerJoinEvent event) {
        if (Hub.getInstance().getConfig().getBoolean("hide-stream")) {
            event.setJoinMessage(null);
            return;
        }
        event.setJoinMessage(Hub.getInstance().getFormat("join-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
        if (Hub.getInstance().getConfig().getBoolean("welcome-messages")) {
            Bukkit.broadcastMessage(Hub.getInstance().getFormat("welcome-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public final void onQuit(PlayerQuitEvent event) {
        if (Hub.getInstance().getConfig().getBoolean("hide-stream")) {
            event.setQuitMessage(null);
            return;
        }
        event.setQuitMessage(Hub.getInstance().getFormat("quit-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public final void onPlayerKick(PlayerKickEvent event) {
        if (Hub.getInstance().getConfig().getBoolean("hide-stream")) {
            event.setLeaveMessage(null);
            return;
        }
        event.setLeaveMessage(Hub.getInstance().getFormat("quit-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
    }

}
