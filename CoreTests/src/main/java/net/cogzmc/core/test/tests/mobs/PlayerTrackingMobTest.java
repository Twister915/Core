package net.cogzmc.core.test.tests.mobs;

import lombok.Data;
import net.cogzmc.core.effect.npc.mobs.MobNPCChicken;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.test.TestModule;
import net.cogzmc.core.util.Point;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public final class PlayerTrackingMobTest implements Listener {
    private final static Random random = new Random();

    private final CPlayer player;
    private final List<MobNPCChicken> chicken = new ArrayList<>();
    private Location lastUpdateLocation = null;
    private Integer lastAngleOffset = 0;

    public void init() {
        TestModule.getInstance().registerListener(this);
        Location location = player.getBukkitPlayer().getLocation();
        for (int i = 0; i < 5; i++) {
            chicken.add(new MobNPCChicken(Point.of(location), location.getWorld(), null, ChatColor.BOLD + ChatColor.AQUA.toString() + "Cary is Love Cary is Life [" + i + "]"));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getPlayer().equals(player.getBukkitPlayer())) return;
        if ((lastUpdateLocation != null && lastUpdateLocation.distanceSquared(event.getTo()) < 1)) return;
        playMoveUpdate();
        lastUpdateLocation = event.getTo();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!event.getPlayer().equals(player.getBukkitPlayer())) return;
        for (MobNPCChicken mobNPCChicken : chicken) {
            mobNPCChicken.despawn();
        }
        chicken.clear();
    }

    private void playMoveUpdate() {
        for (MobNPCChicken mobNPCChicken : chicken) {
            if (!mobNPCChicken.isSpawned()) mobNPCChicken.spawn();
        }
        Point centerPosition = Point.of(player.getBukkitPlayer().getLocation());
        int radius = 4;
        int angularSpacing = 360/chicken.size();
        lastAngleOffset = lastAngleOffset + 11 % 360;
        angularSpacing = angularSpacing % 360;
        for (int i = 0; i < chicken.size(); i++) {
            int angleD = (angularSpacing * i)+lastAngleOffset;
            double angleR = Math.toRadians(angleD);
            double xI = centerPosition.getX() +  Math.cos(angleR)*radius;
            double zI = centerPosition.getZ() +  Math.sin(angleR)*radius;
            MobNPCChicken mobNPCChicken = chicken.get(i);
            mobNPCChicken.move(Point.of(xI, centerPosition.getY() + 1, zI));
        }
        if (random.nextFloat() < 0.3) player.playSoundForPlayer(Sound.CHICKEN_WALK);
    }
}
