package net.cogzmc.bungee;

import java.util.UUID;

public final class PlayerKickManager extends BasePubSub {
    public PlayerKickManager() {
        super("CORE.BUNGEE.KICK");
    }

    @Override
    public void onMessage(String chan, String message) {
        String[] split = message.split(";");
        if (split.length != 2) return;
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(split[0]));
        if (player == null) return;
        player.disconnect(split[1]);
    }

    public static void enable() {
        new PubSubThread(new PlayerKickManager()).start();
    }
}
