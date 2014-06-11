package net.cogzmc.hub.limitations.impl;

import lombok.EqualsAndHashCode;
import net.cogzmc.hub.limitations.Limitation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@EqualsAndHashCode(callSuper = true)
public final class PickupLimitation extends Limitation {
    public PickupLimitation() {
        super("pickup");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (shouldIgnoreLimitation(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (shouldIgnoreLimitation(event.getPlayer())) return;
        event.setCancelled(true);
    }
}
