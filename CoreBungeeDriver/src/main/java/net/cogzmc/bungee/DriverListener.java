package net.cogzmc.bungee;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Log
public final class DriverListener implements Listener {
    private final Set<UUID> connectedPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerConnect(ServerConnectEvent event) {
        if (CoreBungeeDriver.getInstance().getController() == null) return;
        if (!connectedPlayers.contains(event.getPlayer().getUniqueId())) {
            ServerInfo connectServer = CoreBungeeDriver.getInstance().getController().getConnectServer(event.getPlayer());
            event.setTarget(connectServer);
        }
        connectedPlayers.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        connectedPlayers.remove(event.getPlayer().getUniqueId());
    }


    public static void enable() {
        ProxyServer.getInstance().getPluginManager().registerListener(CoreBungeeDriver.getInstance(), new DriverListener());
    }
}
