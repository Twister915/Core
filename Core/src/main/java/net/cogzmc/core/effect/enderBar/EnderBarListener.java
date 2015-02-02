package net.cogzmc.core.effect.enderBar;

import net.cogzmc.core.Core;
import net.cogzmc.core.effect.npc.mobs.MobNPCWither;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import static net.cogzmc.core.effect.enderBar.EnderBarManager.getLocationFor;


@Data
final class EnderBarListener implements Listener {
    private final EnderBarManager manager;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        MobNPCWither wither = manager.witherBar.get(onlinePlayer);
        if (wither == null) return;
        Point current = Point.of(getLocationFor(onlinePlayer));
        if (wither.isSpawned() && current.distanceSquared(wither.getLocation()) > 9) {
            wither.move(current);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        CPlayer player = Core.getOnlinePlayer(event.getPlayer());
        MobNPCWither wither = manager.witherBar.get(player);
        if (wither != null) {
            wither.despawn();
            manager.witherBar.remove(player);
        }
    }
}
