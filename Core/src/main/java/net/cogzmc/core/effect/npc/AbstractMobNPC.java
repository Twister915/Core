package net.cogzmc.core.effect.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import lombok.extern.java.Log;
import net.cogzmc.core.Core;
import net.cogzmc.core.effect.CustomEntityIDManager;
import net.cogzmc.core.effect.npc.packets.*;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import net.cogzmc.util.Observable;
import net.cogzmc.util.RandomUtils;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.*;

@Data
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
@Log
public abstract class AbstractMobNPC implements Observable<NPCObserver> {
    @Getter private Point location;
    @Getter private Integer headYaw;
    @Getter private final World world;
    private final Set<CPlayer> viewers;
    private final Set<NPCObserver> observers;
    protected final WrappedDataWatcher dataWatcher;
    private WrappedDataWatcher lastDataWatcher;
    @Getter boolean spawned;
    @Getter protected final int id;
    private InteractWatcher listener;

    @Getter @Setter private String customName;
    @Setter private Float health = null;
    @Getter @Setter private boolean onFire, crouched, sprinting, blocking, invisible, showingNametag = true;

    protected abstract EntityType getEntityType();
    public abstract Float getMaximumHealth();
    protected void onUpdate() {}
    protected void onDataWatcherUpdate() {}

    {
        SoftNPCManager.getInstance().mobRefs.add(new WeakReference<>(this));
    }

    public AbstractMobNPC(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        this.location = location.deepCopy();
        this.world = world;
        this.viewers = new HashSet<>();
        if (observers != null) this.viewers.addAll(observers);  //TODO: In our interest of making our own Game Engine, possibly create some sort of Location based mask for where entities are?
        this.dataWatcher = new WrappedDataWatcher();
        this.observers = new HashSet<>();
        this.spawned = false;
        this.customName = title;
        this.id = CustomEntityIDManager.getNextId();
    }

    private InteractWatcher createNewInteractWatcher() {
        listener = new InteractWatcher(this);
        return listener;
    }

    @Override
    public final void registerObserver(NPCObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public final void unregisterObserver(NPCObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public final ImmutableSet<NPCObserver> getObservers() {
        return ImmutableSet.copyOf(observers);
    }

    public final void addViewer(CPlayer player) {
        this.viewers.add(player);
        if (this.isSpawned()) forceSpawn(player.getBukkitPlayer());
    }

    public final void removeViewer(CPlayer player) {
        this.viewers.remove(player);
        if (this.isSpawned()) forceDespawn(player.getBukkitPlayer());
    }

    public final void makeGlobal() {
        this.viewers.clear();
    }

    public final Float getHealth() {
        return health == null ? getMaximumHealth() : Math.min(getMaximumHealth(), health);
    }

    protected final Player[] getTargets() {
        CPlayer[] cPlayers = (this.viewers.size() == 0 ? Core.getPlayerManager().getOnlinePlayers() : this.viewers).
                toArray(new CPlayer[this.viewers.size()]);
        Player[] players = new Player[cPlayers.length];
        //filter the players
        int x = 0;
        for (int i = 0; i < cPlayers.length; i++) {
            Player bukkitPlayer = cPlayers[x].getBukkitPlayer();
            UUID uid = bukkitPlayer.getWorld().getUID();
            UUID uid1 = this.world != null ? world.getUID() : null;
            if (this.world != null && !uid.equals(uid1)) continue;
            players[x] = bukkitPlayer;
            x++;
        }
        return x == 0 ? new Player[]{} :  Arrays.copyOfRange(players, 0, x);
    }

    public final void spawn() {
        if (spawned) throw new IllegalStateException("This NPC is already spawned!");
        ProtocolLibrary.getProtocolManager().addPacketListener(createNewInteractWatcher());
        WrapperPlayServerSpawnEntityLiving packet = getSpawnPacket();
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
        spawned = true;
        if (Core.DEBUG) log.info("Spawning " + getClass().getSimpleName() + " with ID #" + id);
    }

    private WrapperPlayServerEntityDestroy getDespawnPacket() {
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        packet.setEntities(new int[]{id});
        return packet;
    }

    public final void despawn() {
        if (!spawned) throw new IllegalStateException("You cannot despawn something that you have not spawned!");
        WrapperPlayServerEntityDestroy packet = getDespawnPacket();
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        listener = null;
        spawned = false;
        if (Core.DEBUG) log.info("Despawned #" + id + " " + getClass().getSimpleName());
    }

    public final void forceDespawn(Player bukkitPlayer) {
        getDespawnPacket().sendPacket(bukkitPlayer);
    }

    public final void forceSpawn(Player player) {
        getSpawnPacket().sendPacket(player);
    }

    protected final WrapperPlayServerSpawnEntityLiving getSpawnPacket() {
        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setEntityID(id);
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        packet.setHeadPitch(location.getPitch()); //TODO check over this and set a default, or get an enum of different directions the head can be.
        packet.setHeadYaw(location.getYaw());
        updateDataWatcher();
        packet.setMetadata(dataWatcher);
        packet.setType(getEntityType());
        return packet;
    }

    private WrapperPlayServerEntityStatus getStatusPacket(Integer status) {
        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityId(id);
        packet.setEntityStatus(status);
        return packet;
    }

    protected final void playStatus(Set<CPlayer> players, Integer status) {
        WrapperPlayServerEntityStatus packet = getStatusPacket(status);
        Player[] targets = getTargets();
        for (CPlayer player : players) {
            Player bukkitPlayer = player.getBukkitPlayer();
            if (viewers.size() == 0 || RandomUtils.contains(targets, bukkitPlayer)) packet.sendPacket(bukkitPlayer);
        }
    }

    protected final void playStatus(Integer status) {
        WrapperPlayServerEntityStatus packet = getStatusPacket(status);
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    public final void playHurtAnimation() {
        playStatus(2);
    }
    public final void playDeadAnimation() {
        playStatus(3);
    }

    public final void playHurtAnimation(Set<CPlayer> players) {
        playStatus(players, 2);
    }
    public final void playDeadAnimation(Set<CPlayer> players) {
        playStatus(players, 3);
    }

    public final void update() {
        if (!spawned) spawn();
        updateDataWatcher(); //Send a call to update the datawatcher
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
        List<WrappedWatchableObject> watchableObjects = new ArrayList<>();
        //Let's check through the last datawatcher values sent (that we store later in this method) and sent changed values.
        if (lastDataWatcher == null) watchableObjects.addAll(dataWatcher.getWatchableObjects());
        else {
            for (WrappedWatchableObject watchableObject : dataWatcher.getWatchableObjects()) {
                Object object = lastDataWatcher.getObject(watchableObject.getIndex());
                if (object == null || !object.equals(watchableObject.getValue())) watchableObjects.add(watchableObject);
            }
        }
        if (Core.DEBUG) {
            for (WrappedWatchableObject watchableObject : watchableObjects) {
                log.info("Sending update on " + watchableObject.getIndex() + " for #" + id + " (" + getClass().getSimpleName() + " ) =" + watchableObject.getValue() + " (" + watchableObject.getType().getName() + ")");
            }
        }
        packet.setEntityMetadata(watchableObjects);
        packet.setEntityId(id);
        for (Player player : getTargets()) {
            packet.sendPacket(player); //Broadcast the packet
        }
        lastDataWatcher = dataWatcher.deepClone(); //So things are only sent when we need to, track changes.
        onUpdate();
    }

    public final void move(Point point) {
        if (!spawned) throw new IllegalStateException("You cannot teleport something that has yet to spawn!");
        final Point location1 = this.location;
        this.location = point;
        AbstractPacket packet;
        if (Core.DEBUG) log.info("Moving from " + location1.toString() + " to " + point.toString() + "; distance=" + location1.distance(point));
        if (location1.distanceSquared(point) <= 16) { //if we're moving less than four blocks
            if (Core.DEBUG) log.info("Teleporting #" + id + " using relative move. " + getClass().getSimpleName());
            WrapperPlayServerEntityMoveLook packet1 = new WrapperPlayServerEntityMoveLook();
            packet1.setEntityID(id);
            packet1.setDx(point.getX() - location1.getX());
            packet1.setDy(point.getY() - location1.getY());
            packet1.setDz(point.getZ() - location1.getZ());
            packet1.setPitch(point.getPitch()); //Pitch of the head
            packet1.setYaw(point.getYaw()); //yaw of the body
            packet = packet1;
        }
        else {
            if (Core.DEBUG) log.info("Teleporting #" + id + " using teleport move. " + getClass().getSimpleName());
            WrapperPlayServerEntityTeleport packet1 = new WrapperPlayServerEntityTeleport();
            packet1.setEntityID(id);
            packet1.setX(point.getX());
            packet1.setY(point.getY());
            packet1.setZ(point.getZ());
            packet1.setPitch(point.getPitch());
            packet1.setYaw(point.getYaw());
            packet = packet1;
        }
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    public final void addVelocity(Vector vector) {
        WrapperPlayServerEntityVelocity packet = new WrapperPlayServerEntityVelocity();
        packet.setEntityId(id);
        packet.setVelocityX(vector.getX());
        packet.setVelocityY(vector.getY());
        packet.setVelocityZ(vector.getZ());
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    public final void moveHead(byte parts) {
        if (!spawned) throw new IllegalStateException("You cannot modify the rotation of the head of a non-spawned entity!");
        headYaw = headYaw + parts;
        WrapperPlayServerEntityHeadRotation packet = new WrapperPlayServerEntityHeadRotation();
        packet.setEntityId(id);
        packet.setHeadYaw(headYaw);
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    protected final void updateDataWatcher() {
        if (Core.DEBUG) log.info("Update for datawatcher called on " + getClass().getSimpleName() + " #" + id + "!");
        dataWatcher.setObject(6, getHealth()); //Health
        if (showingNametag) dataWatcher.setObject(3, (byte)1); //Always show nametag
        else if (dataWatcher.getObject(3) != null) dataWatcher.removeObject(3);
        if (customName != null) dataWatcher.setObject(10, customName.substring(0, Math.min(customName.length(), 64))); //Nametag value
        else if (dataWatcher.getObject(10) != null) dataWatcher.removeObject(10);
        if (customName != null) dataWatcher.setObject(2, customName.substring(0, Math.min(customName.length(), 64))); //Nametag value
        else if (dataWatcher.getObject(10) != null) dataWatcher.removeObject(2);
        //Others
        byte zeroByte = 0;
        if (onFire) zeroByte |= 0x01;
        if (crouched) zeroByte |= 0x02;
        if (sprinting) zeroByte |= 0x08;
        if (blocking) zeroByte |= 0x10;
        if (invisible) zeroByte |= 0x20;
        dataWatcher.setObject(0, zeroByte);
        onDataWatcherUpdate();
    }

    public final ImmutableSet<CPlayer> getViewers() {
        return ImmutableSet.copyOf(viewers);
    }

    private static class InteractWatcher extends PacketAdapter {
        private final AbstractMobNPC watchingFor;

        public InteractWatcher(AbstractMobNPC watchingFor) {
            super(Core.getInstance(), PacketType.Play.Client.USE_ENTITY);
            this.watchingFor = watchingFor;
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
            if (packet.getTargetID() != watchingFor.getId()) return;
            CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
            ClickAction clickAction = ClickAction.valueOf(packet.getMouse());
            for (NPCObserver npcObserver : watchingFor.getObservers()) {
                try {npcObserver.onPlayerInteract(onlinePlayer, watchingFor, clickAction);} catch (Exception e) {e.printStackTrace();}
            }
            event.setCancelled(true);
        }
    }
}
