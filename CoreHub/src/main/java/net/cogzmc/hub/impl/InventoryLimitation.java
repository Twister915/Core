package net.cogzmc.hub.impl;

import lombok.EqualsAndHashCode;
import net.cogzmc.hub.Limitation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

@EqualsAndHashCode(callSuper = true)
public final class InventoryLimitation extends Limitation {
    public InventoryLimitation() {
        super("no-inventory-interact");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInventoryInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (shouldIgnoreLimitation((Player) event.getWhoClicked())) return;
        event.setCancelled(true);
    }
}
