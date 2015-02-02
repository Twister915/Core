package net.cogzmc.hub.impl;

import net.cogzmc.hub.Limitation;

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