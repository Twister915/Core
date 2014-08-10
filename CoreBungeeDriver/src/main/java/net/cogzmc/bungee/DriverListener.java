package net.cogzmc.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DriverListener implements Listener {
    private final Set<UUID> connectedPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerConnect(ServerConnectEvent event) {
        if (CoreBungeeDriver.getInstance().getController() == null) return;
        if (!connectedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setTarget(CoreBungeeDriver.getInstance().getController().getConnectServer(event.getPlayer()));
        }
        connectedPlayers.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        connectedPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuery(ProxyPingEvent event) {
        event.getResponse().getPlayers().setOnline(CoreBungeeDriver.getInstance().getServerManager().getOnlineCount());
    }

    public static void enable() {
        ProxyServer.getInstance().getPluginManager().registerListener(CoreBungeeDriver.getInstance(), new DriverListener());
    }
}
