package net.cogzmc.bungee;

import net.cogzmc.core.player.COfflinePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Map;

public final class PermissionsHandler implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        COfflinePlayer offlinePlayerByUUID = CoreBungeeDriver.getInstance().getPlayerRepository().getOfflinePlayerByUUID(player.getUniqueId());
        for (Map.Entry<String, Boolean> perm : offlinePlayerByUUID.getAllPermissions().entrySet()) {
            player.setPermission(perm.getKey(), perm.getValue());
        }
    }

    public static void enable() {
        ProxyServer.getInstance().getPluginManager().registerListener(CoreBungeeDriver.getInstance(), new PermissionsHandler());
    }
}
