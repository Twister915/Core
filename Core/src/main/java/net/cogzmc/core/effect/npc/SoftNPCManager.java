package net.cogzmc.core.effect.npc;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import net.cogzmc.util.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SoftNPCManager implements CPlayerConnectionListener, Listener {
    @Getter private static SoftNPCManager instance;

    final Set<WeakReference<AbstractMobNPC>> mobRefs = new HashSet<>();

    public SoftNPCManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
        Core.getPlayerManager().registerCPlayerConnectionListener(this);
    }

    private void ensureAllValid() {
        Iterator<WeakReference<AbstractMobNPC>> iterator = mobRefs.iterator();
        while (iterator.hasNext()) {
            WeakReference<AbstractMobNPC> mob = iterator.next();
            if (mob.get() == null) iterator.remove();
        }
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ensureAllValid();
        for (WeakReference<AbstractMobNPC> mobRef : mobRefs) {
            final AbstractMobNPC npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (npcMob.isSpawned() && npcMob.getViewers().size() == 0) {
                npcMob.forceSpawn(event.getPlayer());
            }
        }
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        ensureAllValid();
        for (WeakReference<AbstractMobNPC> mobRef : mobRefs) {
            final AbstractMobNPC npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (npcMob.getViewers().contains(player)) npcMob.removeViewer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ensureAllValid();
        final CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        for (WeakReference<AbstractMobNPC> mobRef : mobRefs) {
            final AbstractMobNPC npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (!npcMob.isSpawned()) continue;
            if (!npcMob.getWorld().equals(event.getRespawnLocation().getWorld())) continue;
            Bukkit.getScheduler().runTask(Core.getInstance(), new Runnable() {
                @Override
                public void run() {
                    npcMob.forceSpawn(onlinePlayer.getBukkitPlayer());
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        ensureAllValid();
        for (WeakReference<AbstractMobNPC> mobRef : mobRefs) {
            AbstractMobNPC mobNPC = mobRef.get();
            if (mobNPC == null || mobNPC.getViewers().size() != 0 && RandomUtils.contains(mobNPC.getTargets(), event.getPlayer())) continue;
            if (mobNPC.getWorld() == null || event.getPlayer().getWorld().equals(mobNPC.getWorld()))
                mobNPC.forceSpawn(event.getPlayer());
        }
    }

    public void removeAllEntities() {
        ensureAllValid();
        LinkedHashSet<Integer> ids = new LinkedHashSet<>();
        for (WeakReference<AbstractMobNPC> villagerRef : mobRefs) {
            AbstractMobNPC abstractMobNPC = villagerRef.get();
            if (abstractMobNPC == null) continue;
            ids.add(abstractMobNPC.getId());
            abstractMobNPC.spawned = false;
        }
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        int[] idsArray = new int[ids.size()];
        Integer[] idsIntegerArray = ids.toArray(new Integer[ids.size()]);
        for (int x = 0; x < ids.size(); x++) {
            idsArray[x] = idsIntegerArray[x];
        }
        packet.setEntities(idsArray);
        for (Player player : Bukkit.getOnlinePlayers()) {
            packet.sendPacket(player);
        }
    }
}
