package net.cogzmc.hub.impl;

import lombok.EqualsAndHashCode;
import net.cogzmc.hub.Limitation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@EqualsAndHashCode(callSuper = true)
public final class StreamLimitation extends Limitation {
    public StreamLimitation() {
        super("no-stream");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }
}
