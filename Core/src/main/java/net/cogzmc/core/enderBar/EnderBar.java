 package net.cogzmc.core.enderBar;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Data
final class EnderBar {
    private final CPlayer player;
    private final Integer id;

    private String text;
    private Float health = 1f;
    @Setter(AccessLevel.NONE) private boolean spawned;
    @Setter(AccessLevel.NONE) private Location currentLocation;
    private final WrappedDataWatcher dataWatcher = new WrappedDataWatcher();

    void spawn() {
        if (spawned) throw new IllegalStateException("You cannot spawn this entity twice!");
        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setHeadYaw(0f);
        packet.setHeadPitch(0f);
        packet.setEntityID(id);
        updateDataWatcher();
        packet.setMetadata(dataWatcher);
        packet.setType(EntityType.ENDER_DRAGON);
        currentLocation = getBestLocation();
        packet.setX(currentLocation.getX());
        packet.setY(currentLocation.getY());
        packet.setZ(currentLocation.getZ());
        packet.sendPacket(player.getBukkitPlayer());
        spawned = true;
    }

    void updateLocation() {
        if (!spawned) return;
        WrapperPlayServerEntityTeleport entityTeleport = new WrapperPlayServerEntityTeleport();
        entityTeleport.setEntityID(id);
        currentLocation = getBestLocation();
        entityTeleport.setX(currentLocation.getX());
        entityTeleport.setY(currentLocation.getY());
        entityTeleport.setZ(currentLocation.getZ());
        entityTeleport.setPitch(0f);
        entityTeleport.setYaw(0f);
        entityTeleport.sendPacket(player.getBukkitPlayer());
    }

    void remove() {
        if (!spawned) throw new IllegalStateException("You cannot remove an entity that has not spawned!");
        WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
        destroyPacket.setEntities(new int[]{id});
        destroyPacket.sendPacket(player.getBukkitPlayer());
        spawned = false;
    }

    void update() {
        if (!spawned) return;
        updateDataWatcher();
        WrapperPlayServerEntityMetadata metaPacket = new WrapperPlayServerEntityMetadata();
        metaPacket.setEntityId(id);
        metaPacket.setEntityMetadata(dataWatcher.getWatchableObjects());
        metaPacket.sendPacket(player.getBukkitPlayer());
    }

    void newWorld() {
        if (!spawned) return;
        spawned = false;
        spawn();
    }

    void setText(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&',text.substring(0, Math.min(64, text.length())));
        update();
    }

    void setHealth(Float health) {
        this.health = health;
        update();
    }

    Location getBestLocation() {
        return player.getBukkitPlayer().getLocation().clone().add(0, -300, 0);
    }

    private void updateDataWatcher() {
        dataWatcher.setObject(6, health*200f); //Set the health (health is a %, 200 is the actual health)
        if (text != null) dataWatcher.setObject(10, text); //Set the display name text now.
    }

    public void respawn() {
        if (!spawned) return;
        this.spawned = false;
        spawn();
    }
}
