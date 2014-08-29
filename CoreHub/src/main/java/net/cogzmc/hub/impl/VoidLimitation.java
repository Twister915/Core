package net.cogzmc.hub.impl;

import net.cogzmc.core.Core;
import net.cogzmc.hub.Limitation;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public final class VoidLimitation extends Limitation {
    public VoidLimitation() {
        super("no-fall-void");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getY() > 1) return; //This first because it's a more exclusive case and easier to test than doing a permissions check.
        if (shouldIgnoreLimitation(event)) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        player.teleport(event.getTo().getWorld().getSpawnLocation());
        player.setFallDistance(0f);
        Core.getOnlinePlayer(player).playSoundForPlayer(Sound.CHICKEN_EGG_POP); //A bit intensive but such an exclusive case it doesn't matter.
    }
}
