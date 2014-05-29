package net.cogzmc.coreessentials.commands;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.mongo.CMongoPermissionsManager;
import net.cogzmc.core.player.mongo.GroupReloadObserver;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class TabColorManager implements Listener, GroupReloadObserver {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerListName(Core.getOnlinePlayer(event.getPlayer()));
    }

    void updatePlayerListName(CPlayer player) {
        ChatColor tablistColor = player.getTablistColor();
        player.getBukkitPlayer().setPlayerListName((tablistColor == null ? "" : tablistColor) + player.getDisplayName());
    }

    @Override
    public void onReloadPermissions(CMongoPermissionsManager manager) {
        for (CPlayer cPlayer : Core.getPlayerManager()) {
            updatePlayerListName(cPlayer);
        }
    }
}
