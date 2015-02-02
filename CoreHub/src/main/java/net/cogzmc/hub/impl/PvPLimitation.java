package net.cogzmc.hub.impl;

import net.cogzmc.hub.Limitation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class PvPLimitation extends Limitation {
    public PvPLimitation() {
        super("no-pvp");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPvP(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player || event.getEntity() instanceof Player) event.setCancelled(true);
    }
}
