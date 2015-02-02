package net.cogzmc.hub.impl;

import net.cogzmc.hub.Limitation;

public final class HungerLimitation extends Limitation {
    public HungerLimitation() {
        super("no-hunger");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if (shouldIgnoreLimitation(event)) return;
        event.getPlayer().setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerHungerChange(FoodLevelChangeEvent event) {
        Player entity = (Player) event.getEntity();
        if (!(entity instanceof Player)) return;
        if (entity.getFoodLevel() != 20) entity.setFoodLevel(20);
        event.setCancelled(true);
    }
}
