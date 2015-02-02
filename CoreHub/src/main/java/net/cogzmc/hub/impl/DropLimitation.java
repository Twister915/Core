package net.cogzmc.hub.impl;

import net.cogzmc.hub.Limitation;

@EqualsAndHashCode(callSuper = true)
public final class DropLimitation extends Limitation {
    public DropLimitation() {
        super("no-drop");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (shouldIgnoreLimitation(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (shouldIgnoreLimitation(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPickupEXP(PlayerExpChangeEvent event) {
        if (shouldIgnoreLimitation(event)) return;
        event.setAmount(0);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDropItem(EntityDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
    }
}
