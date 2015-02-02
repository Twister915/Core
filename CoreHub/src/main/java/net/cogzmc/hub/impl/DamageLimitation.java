package net.cogzmc.hub.impl;

import net.cogzmc.hub.Limitation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

public final class DamageLimitation extends Limitation {
    public DamageLimitation() {
        super("no-damage");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        switch (event.getCause()) {
            case VOID:
            case CUSTOM:
            case SUICIDE:
            return;
        }
        event.setCancelled(true);
    }
}