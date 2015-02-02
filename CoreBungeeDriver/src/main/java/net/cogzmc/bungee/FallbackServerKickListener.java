package net.cogzmc.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class FallbackServerKickListener implements Listener {
    @EventHandler
    public void onPlayerKick(ServerKickEvent event) {
        if (event.getCancelServer() != null || CoreBungeeDriver.getInstance().getController() == null) return;
        ServerInfo fallbackServer = CoreBungeeDriver.getInstance().getController().getFallbackServer(event.getPlayer());
        if (fallbackServer == null) return;
        event.setCancelServer(fallbackServer);
        event.setCancelled(true);
    }

    public static void enable() {
        CoreBungeeDriver instance = CoreBungeeDriver.getInstance();
        instance.getProxy().getPluginManager().registerListener(instance, new FallbackServerKickListener());
    }
}
