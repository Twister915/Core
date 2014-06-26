package net.cogzmc.core.effect.npc;

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
import java.util.Set;

public final class SoftVillagerManager implements CPlayerConnectionListener, Listener {
    @Getter private static SoftVillagerManager instance;

    final Set<WeakReference<NPCVillager>> villagerRefs = new HashSet<>();

    public SoftVillagerManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
        Core.getPlayerManager().registerCPlayerConnectionListener(this);
    }

    private void ensureAllValid() {
        Iterator<WeakReference<NPCVillager>> iterator = villagerRefs.iterator();
        while (iterator.hasNext()) {
            WeakReference<NPCVillager> next = iterator.next();
            if (next.get() == null) iterator.remove();
        }
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ensureAllValid();
        for (WeakReference<NPCVillager> villagerRef : villagerRefs) {
            final NPCVillager npcVillager = villagerRef.get();
            if (npcVillager == null) continue;
            if (npcVillager.isSpawned() && npcVillager.getViewers().size() == 0) {
                npcVillager.forceSpawn(event.getPlayer());
            }
        }
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        ensureAllValid();
        for (WeakReference<NPCVillager> villagerRef : villagerRefs) {
            final NPCVillager villager = villagerRef.get();
            if (villager == null) continue;
            if (villager.getViewers().contains(player)) villager.removeViewer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ensureAllValid();
        final CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        for (WeakReference<NPCVillager> villagerRef : villagerRefs) {
            final NPCVillager villager = villagerRef.get();
            if (villager == null) continue;
            if (!villager.getWorld().equals(event.getRespawnLocation().getWorld())) continue;
            Bukkit.getScheduler().runTask(Core.getInstance(), new Runnable() {
                @Override
                public void run() {
                    villager.forceSpawn(onlinePlayer.getBukkitPlayer());
                }
            });
        }
    }
}
