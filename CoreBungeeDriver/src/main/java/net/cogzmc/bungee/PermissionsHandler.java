package net.cogzmc.bungee;

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
