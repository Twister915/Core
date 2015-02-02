package net.cogzmc.hub.impl;

import net.cogzmc.hub.Limitation;

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
