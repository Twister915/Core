package net.cogzmc.hub.limitations.impl;

import lombok.EqualsAndHashCode;
import net.cogzmc.hub.limitations.Limitation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

@EqualsAndHashCode(callSuper = true)
public final class InventoryLimitation extends Limitation {
    public InventoryLimitation() {
        super("limit-inventory");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInventoryInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (shouldIgnoreLimitation((Player) event.getWhoClicked())) return;
        event.setCancelled(true);
    }
}
