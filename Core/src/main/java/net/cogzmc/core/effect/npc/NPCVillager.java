package net.cogzmc.core.effect.npc;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import net.cogzmc.core.Core;
import net.cogzmc.core.effect.CustomEntityIDManager;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import net.cogzmc.util.Observable;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.entity.Villager.Profession;

@Data
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public final class NPCVillager implements Observable<NPCObserver> {
    @Getter private Point location;
    private final Set<CPlayer> viewers;
    private final Set<NPCObserver> observers;
    private final WrappedDataWatcher dataWatcher;
    @Getter private boolean spawned;
    @Getter private final int id;
    @Getter private String customName;
    @Getter private Profession profession;
    private InteractWatcher listener;

    {
        SoftVillagerManager.getInstance().villagerRefs.add(new WeakReference<>(this));
    }

    public NPCVillager(Point location, Set<CPlayer> observers, String title) {
        this(location, observers, title, null);
    }

    public NPCVillager(@NonNull Point location, Set<CPlayer> observers, @NonNull String title, Profession profession) {
        this.location = location;
        this.viewers = new HashSet<>();
        if (observers != null) this.viewers.addAll(observers);
        this.dataWatcher = new WrappedDataWatcher();
        this.observers = new HashSet<>();
        this.spawned = false;
        this.customName = title;
        this.id = CustomEntityIDManager.getNextId();
        this.profession = profession;
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

    private Player[] getTargets() {
        if (this.viewers.size() == 0) return Bukkit.getOnlinePlayers();
        else {
            CPlayer[] cPlayers = this.viewers.toArray(new CPlayer[this.viewers.size()]);
            Player[] players = new Player[cPlayers.length];
            for (int x = 0; x < cPlayers.length; x++) {
                players[x] = cPlayers[x].getBukkitPlayer();
            }
            return players;
        }
    }

    public void spawn() {
        if (spawned) throw new IllegalStateException("This NPC is already spawned!");
        ProtocolLibrary.getProtocolManager().addPacketListener(createNewInteractWatcher());
        WrapperPlayServerSpawnEntityLiving packet = getSpawnPacket();
        for (Player player : getTargets()) {
            packet.sendPacket(player);
        }
        spawned = true;
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

    private WrapperPlayServerSpawnEntityLiving getSpawnPacket() {
        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setEntityID(id);
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        packet.setHeadPitch(location.getPitch()); //TODO check over this and set a default, or get an enum of different directions the head can be.
        packet.setHeadYaw(location.getYaw());
        updateDataWatcher();
        packet.setMetadata(dataWatcher);
        packet.setType(EntityType.VILLAGER);
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

    private void updateDataWatcher() {
        dataWatcher.setObject(6, 20f); //Health
        dataWatcher.setObject(11, customName == null ? 0 : (byte)1); //Always show nametag
        if (customName != null) dataWatcher.setObject(10, customName); //Nametag value
        else if (dataWatcher.getObject(10) != null) dataWatcher.removeObject(10);
        dataWatcher.setObject(12, 1); //Age (adult)
        if (profession != null) dataWatcher.setObject(16, profession.getId()); //Profession
        else if (dataWatcher.getObject(16) != null) dataWatcher.removeObject(16);
    }

    public void setCustomName(String name) {
        this.customName = name;
        updateDataWatcher();
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
        updateDataWatcher();
    }

    public ImmutableSet<CPlayer> getViewers() {
        return ImmutableSet.copyOf(viewers);
    }

    private static class InteractWatcher extends PacketAdapter {
        private final NPCVillager watchingFor;

        public InteractWatcher(NPCVillager watchingFor) {
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
        }
    }
}
