package net.cogzmc.bungee;

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
