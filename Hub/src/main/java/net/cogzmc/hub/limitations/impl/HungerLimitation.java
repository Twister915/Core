package net.cogzmc.hub.limitations.impl;

import net.cogzmc.hub.limitations.Limitation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

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
