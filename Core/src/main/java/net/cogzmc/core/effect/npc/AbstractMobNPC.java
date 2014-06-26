package net.cogzmc.core.effect.npc;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import lombok.extern.java.Log;
import net.cogzmc.core.Core;
import net.cogzmc.core.effect.CustomEntityIDManager;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import net.cogzmc.util.Observable;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
@Log(topic = "Abstract Mob Log")
public abstract class AbstractMobNPC implements Observable<NPCObserver> {
    @Getter private Point location;
    @Getter private final World world;
    private final Set<CPlayer> viewers;
    private final Set<NPCObserver> observers;
    protected final WrappedDataWatcher dataWatcher;
    @Getter private boolean spawned;
    @Getter protected final int id;
    @Getter @Setter private String customName;
    @Getter @Setter private Float health = getMaximumHealth();
    @Getter @Setter private boolean showingNametag = true;
    private InteractWatcher listener;

    protected abstract EntityType getEntityType();
    protected abstract Float getMaximumHealth();
    protected void onUpdate() {}
    protected void onDataWatcherUpdate() {}

    {
        SoftNPCManager.getInstance().villagerRefs.add(new WeakReference<>(this));
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
        updateDataWatcher();
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
    }

    public void removeViewer(CPlayer player) {
        this.viewers.remove(player);
    }

    public void makeGlobal() {
        this.viewers.clear();
    }

    protected Player[] getTargets() {
        CPlayer[] cPlayers = (this.viewers.size() == 0 ? Core.getPlayerManager().getOnlinePlayers() : this.viewers).
                toArray(new CPlayer[this.viewers.size()]);
        Player[] players = new Player[cPlayers.length];
        //filter the players
        for (int x = 0; x < cPlayers.length; x++) {
            Player bukkitPlayer = cPlayers[x].getBukkitPlayer();
            if (!bukkitPlayer.getWorld().equals(world)) continue;
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
        log.info("Spawning " + packet.getType() + " with ID #" + id);
    }

    public void despawn() {
        if (!spawned) throw new IllegalStateException("You cannot despawn something that you have not spawned!");
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        packet.setEntities(new int[]{id});
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        listener = null;
        spawned = false;
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

    public void update() {
        if (!spawned) spawn();
        updateDataWatcher();
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
        packet.setEntityMetadata(dataWatcher.getWatchableObjects());
        packet.setEntityId(id);
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
        onUpdate();
    }

    public void move(Point point) {
        if (!spawned) throw new IllegalStateException("You cannot teleport something that has yet to spawn!");
        final Point location1 = this.location;
        this.location = point;
        AbstractPacket packet;
        if (location1.distanceSquared(point) > 16) { //if we're moving more than four blocks
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

    protected void updateDataWatcher() {
        dataWatcher.setObject(6, Math.min(health, getMaximumHealth())); //Health
        if (showingNametag) dataWatcher.setObject(11, (byte)1); //Always show nametag
        else if (dataWatcher.getObject(11) != null) dataWatcher.removeObject(11);
        if (customName != null) dataWatcher.setObject(10, customName.substring(0, Math.min(customName.length(), 64))); //Nametag value
        else if (dataWatcher.getObject(10) != null) dataWatcher.removeObject(10);
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
