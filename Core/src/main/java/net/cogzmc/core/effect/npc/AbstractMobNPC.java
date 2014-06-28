package net.cogzmc.core.effect.npc;

import com.comphenix.packetwrapper.*;
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
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import net.cogzmc.util.Observable;
import net.cogzmc.util.RandomUtils;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
@Log(topic = "Abstract Mob Log")
public abstract class AbstractMobNPC implements Observable<NPCObserver> {
    @Getter private Point location;
    @Getter private Integer headRotation;
    @Getter private final World world;
    private final Set<CPlayer> viewers;
    private final Set<NPCObserver> observers;
    protected final WrappedDataWatcher dataWatcher;
    private WrappedDataWatcher lastDataWatcher;
    @Getter boolean spawned;
    @Getter protected final int id;
    private InteractWatcher listener;

    @Getter @Setter private String customName;
    @Getter @Setter private boolean showingNametag = true;
    @Setter private Float health = null;
    @Getter @Setter private boolean onFire;
    @Getter @Setter private boolean crouched;
    @Getter @Setter private boolean sprinting;
    @Getter @Setter private boolean blocking;
    @Getter @Setter private boolean invisible;

    protected abstract EntityType getEntityType();
    protected abstract Float getMaximumHealth();
    protected void onUpdate() {}
    protected void onDataWatcherUpdate() {}

    {
        SoftNPCManager.getInstance().mobRefs.add(new WeakReference<>(this));
    }

    public AbstractMobNPC(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        this.location = location;
        this.world = world;
        this.viewers = new HashSet<>();
        if (observers != null) this.viewers.addAll(observers);
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
    public void registerObserver(NPCObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void unregisterObserver(NPCObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public ImmutableSet<NPCObserver> getObservers() {
        return ImmutableSet.copyOf(observers);
    }

    public void addViewer(CPlayer player) {
        this.viewers.add(player);
        if (this.isSpawned()) forceSpawn(player.getBukkitPlayer());
    }

    public void removeViewer(CPlayer player) {
        this.viewers.remove(player);
        if (this.isSpawned()) forceDespawn(player.getBukkitPlayer());
    }

    public void makeGlobal() {
        this.viewers.clear();
    }

    public Float getHealth() {
        return health == null ? getMaximumHealth() : Math.min(getMaximumHealth(), health);
    }

    protected Player[] getTargets() {
        CPlayer[] cPlayers = (this.viewers.size() == 0 ? Core.getPlayerManager().getOnlinePlayers() : this.viewers).
                toArray(new CPlayer[this.viewers.size()]);
        Player[] players = new Player[cPlayers.length];
        //filter the players
        for (int x = 0; x < cPlayers.length; x++) {
            Player bukkitPlayer = cPlayers[x].getBukkitPlayer();
            if (this.world != null && !bukkitPlayer.getWorld().equals(world)) continue;
            players[x] = bukkitPlayer;
        }
        return players;
    }

    public void spawn() {
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

    public void despawn() {
        if (!spawned) throw new IllegalStateException("You cannot despawn something that you have not spawned!");
        WrapperPlayServerEntityDestroy packet = getDespawnPacket();
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        listener = null;
        spawned = false;
        log.info("Despawned #" + id + " " + getClass().getSimpleName());
    }

    public void forceDespawn(Player bukkitPlayer) {
        getDespawnPacket().sendPacket(bukkitPlayer);
    }

    public void forceSpawn(Player player) {
        getSpawnPacket().sendPacket(player);
    }

    protected WrapperPlayServerSpawnEntityLiving getSpawnPacket() {
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

    protected void playStatus(Set<CPlayer> players, Integer status) {
        WrapperPlayServerEntityStatus packet = getStatusPacket(status);
        Player[] targets = getTargets();
        for (CPlayer player : players) {
            Player bukkitPlayer = player.getBukkitPlayer();
            if (viewers.size() == 0 || RandomUtils.contains(targets, bukkitPlayer)) packet.sendPacket(bukkitPlayer);
        }
    }

    protected void playStatus(Integer status) {
        WrapperPlayServerEntityStatus packet = getStatusPacket(status);
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    public void playHurtAnimation() {
        playStatus(2);
    }

    public void playDeadAnimation() {
        playStatus(3);
    }
    public void playHurtAnimation(Set<CPlayer> players) {
        playStatus(players, 2);
    }

    public void playDeadAnimation(Set<CPlayer> players) {
        playStatus(players, 3);
    }

    public void update() {
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

    public void move(Point point) {
        if (!spawned) throw new IllegalStateException("You cannot teleport something that has yet to spawn!");
        final Point location1 = this.location;
        this.location = point;
        AbstractPacket packet;
        if (Core.DEBUG) log.info("Moving from " + location1.toString() + " to " + point.toString() + "; distance=" + location1.distance(point));
        if (location1.distanceSquared(point) <= 16) { //if we're moving less than four blocks
            if (Core.DEBUG) log.info("Teleporting #" + id + " using relative move. " + getClass().getSimpleName());
            WrapperPlayServerEntityMoveLook packet1 = new WrapperPlayServerEntityMoveLook();
            packet1.setEntityID(id);
            packet1.setDx(location1.getX() - point.getX());
            packet1.setDy(location1.getY() - point.getY());
            packet1.setDz(location1.getZ() - point.getZ());
            packet1.setPitch(point.getPitch());
            packet1.setYaw(point.getYaw());
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

    public void addVelocity(Vector vector) {
        WrapperPlayServerEntityVelocity packet = new WrapperPlayServerEntityVelocity();
        packet.setEntityId(id);
        packet.setVelocityX(vector.getX());
        packet.setVelocityY(vector.getY());
        packet.setVelocityZ(vector.getZ());
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    public void moveHead(byte parts) {
        if (!spawned) throw new IllegalStateException("You cannot modify the rotation of the head of a non-spawned entity!");
        headRotation = headRotation + parts;
        WrapperPlayServerEntityHeadRotation packet = new WrapperPlayServerEntityHeadRotation();
        packet.setEntityId(id);
        packet.setHeadYaw(headRotation);
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    protected void updateDataWatcher() {
        if (Core.DEBUG) log.info("Update for datawatcher called on " + getClass().getSimpleName() + " #" + id + "!");
        dataWatcher.setObject(6, getHealth()); //Health
        if (showingNametag) dataWatcher.setObject(11, (byte)1); //Always show nametag
        else if (dataWatcher.getObject(11) != null) dataWatcher.removeObject(11);
        if (customName != null) dataWatcher.setObject(10, customName.substring(0, Math.min(customName.length(), 64))); //Nametag value
        else if (dataWatcher.getObject(10) != null) dataWatcher.removeObject(10);
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

    public ImmutableSet<CPlayer> getViewers() {
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
