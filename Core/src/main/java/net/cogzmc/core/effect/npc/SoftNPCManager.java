package net.cogzmc.core.effect.npc;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

    final Set<WeakReference<AbstractMobNPC>> villagerRefs = new HashSet<>();

    public SoftNPCManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
        Core.getPlayerManager().registerCPlayerConnectionListener(this);
    }

    private void ensureAllValid() {
        Iterator<WeakReference<AbstractMobNPC>> iterator = villagerRefs.iterator();
        while (iterator.hasNext()) {
            WeakReference<AbstractMobNPC> next = iterator.next();
            if (next.get() == null) iterator.remove();
        }
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ensureAllValid();
        for (WeakReference<AbstractMobNPC> villagerRef : villagerRefs) {
            final AbstractMobNPC npcVillager = villagerRef.get();
            if (npcVillager == null) continue;
            if (npcVillager.isSpawned() && npcVillager.getViewers().size() == 0) {
                npcVillager.forceSpawn(event.getPlayer());
            }
        }
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        ensureAllValid();
        for (WeakReference<AbstractMobNPC> villagerRef : villagerRefs) {
            final AbstractMobNPC villager = villagerRef.get();
            if (villager == null) continue;
            if (villager.getViewers().contains(player)) villager.removeViewer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ensureAllValid();
        final CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        for (WeakReference<AbstractMobNPC> villagerRef : villagerRefs) {
            final AbstractMobNPC villager = villagerRef.get();
            if (villager == null) continue;
            if (!villager.isSpawned()) continue;
            if (!villager.getWorld().equals(event.getRespawnLocation().getWorld())) continue;
            Bukkit.getScheduler().runTask(Core.getInstance(), new Runnable() {
                @Override
                public void run() {
                    villager.forceSpawn(onlinePlayer.getBukkitPlayer());
                }
            });
        }
    }

    public void removeAllEntities() {
        ensureAllValid();
        LinkedHashSet<Integer> ids = new LinkedHashSet<>();
        for (WeakReference<AbstractMobNPC> villagerRef : villagerRefs) {
            AbstractMobNPC abstractMobNPC = villagerRef.get();
            if (abstractMobNPC == null) continue;
            ids.add(abstractMobNPC.getId());
        }
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        int[] idsArray = new int[ids.size()];
        Integer[] idsIntegerArray = ids.toArray(new Integer[ids.size()]);
        for (int x = 0; x < ids.size(); x++) {
            idsArray[x] = idsIntegerArray[x];
        }
        packet.setEntities(idsArray);
    }
}
