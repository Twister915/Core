package net.cogzmc.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public final class Teleporter extends BasePubSub {
    public Teleporter() {
        super("CORE.BUNGEE.TELEPORT");
    }

    @Override
    public void onMessage(String s, String s2) {
        String[] split = s2.split("\\|");
        if (split.length != 2) return;
        try {
            UUID uuid = UUID.fromString(split[0]);
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            if (player == null) return;
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(split[1]);
            if (serverInfo == null) return;
            player.connect(serverInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void enable() {
        new PubSubThread(new Teleporter()).start();

    }
}
