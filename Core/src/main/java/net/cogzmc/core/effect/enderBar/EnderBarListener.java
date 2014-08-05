package net.cogzmc.core.effect.enderBar;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.effect.npc.mobs.MobNPCEnderDragon;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static net.cogzmc.core.effect.enderBar.EnderBarManager.HEIGHT;

@Data
final class EnderBarListener implements Listener {
    private final EnderBarManager manager;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        MobNPCEnderDragon enderBarFor = manager.enderBars.get(onlinePlayer);
        if (enderBarFor == null) return;
        Point current = Point.of(event.getTo());
        current.setY(HEIGHT);
        current.setPitch(0F);
        current.setYaw(0F);
        if (enderBarFor.isSpawned() && current.distanceSquared(enderBarFor.getLocation()) > 9) {
            enderBarFor.move(current);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        CPlayer player = Core.getOnlinePlayer(event.getPlayer());
        MobNPCEnderDragon enderBarFor = manager.enderBars.get(player);
        if (enderBarFor != null) {
            enderBarFor.despawn();
        }
    }
}
