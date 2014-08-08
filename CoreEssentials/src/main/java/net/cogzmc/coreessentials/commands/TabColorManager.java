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
        String tablistColor = player.getTablistColor() == null ? player.getPrimaryGroup().getTablistColor() : player.getTablistColor();
        String s = (tablistColor == null ? "" : tablistColor) + player.getDisplayName();
        s = s.substring(0, Math.min(16, s.length()));
        player.getBukkitPlayer().setPlayerListName(ChatColor.translateAlternateColorCodes('&', s));
    }

    @Override
    public void onReloadPermissions(CMongoPermissionsManager manager) {
        for (CPlayer cPlayer : Core.getPlayerManager()) {
            updatePlayerListName(cPlayer);
        }
    }
}
