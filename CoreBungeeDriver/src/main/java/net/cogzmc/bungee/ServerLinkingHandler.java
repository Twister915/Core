package net.cogzmc.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

public final class ServerLinkingHandler extends BasePubSub {
    private static final String CORE_LINK = "CORE.BUNGEE.LINK";

    private static final String LINK_MODE = "LINK";
    private static final String UNLINK_MODE = "UNLINK";

    public ServerLinkingHandler() {
        super(CORE_LINK);
    }

    @Override
    public void onMessage(String chan, String message) {
        String[] split = message.split(";");
        if (split.length == 1) return;
        if (split[0].equals(LINK_MODE)) {
            if (split.length != 3) return;
            String name = split[1];
            String ip = split[2];
            String[] address = ip.split(":");
            InetSocketAddress socketAddress = new InetSocketAddress(address[0], Integer.parseInt(address[1]));
            ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, socketAddress, "CORE", false));
        } else if (split[0].equals(UNLINK_MODE)) {
            if (split.length != 2) return;
            String name = split[1];
            ServerInfo serverInfo = ProxyServer.getInstance().getServers().get(name);
            if (serverInfo == null) return;
            for (ProxiedPlayer proxiedPlayer : serverInfo.getPlayers()) {
                proxiedPlayer.connect(CoreBungeeDriver.getInstance().getController().getFallbackServer(proxiedPlayer));
            }
            ProxyServer.getInstance().getServers().remove(name);
        }
    }

    public static void enable() {
        new ServerLinkingHandler();
    }
}
