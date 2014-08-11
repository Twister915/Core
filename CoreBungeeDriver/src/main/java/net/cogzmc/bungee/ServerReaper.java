package net.cogzmc.bungee;

import lombok.extern.java.Log;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log
public final class ServerReaper extends BasePubSub implements Runnable {
    private static final String REAP_CHANNEL = "CORE.BUNGEE.REAP";

    private Integer playerCount = 0;
    private Integer lastLength = 0;

    private volatile Map<String, Integer> playerCounts = new HashMap<>();
    private final Collection<ServerInfo> preserve;


    protected ServerReaper(Collection<ServerInfo> preserve) {
        super(REAP_CHANNEL);
        this.preserve = preserve;
    }

    @Override
    public void run() {
        for (final ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            serverInfo.ping(new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    if (throwable != null) {
                        if (preserve.contains(serverInfo)) return;
                        Jedis jedisClient = CoreBungeeDriver.getInstance().getJedisClient();
                        jedisClient.publish(REAP_CHANNEL,serverInfo.getName());
                        CoreBungeeDriver.getInstance().returnJedis(jedisClient);
                        playerCounts.remove(serverInfo.getName());
                        log.info("Server was non-responsive, and removed " + serverInfo.getName());
                    } else {
                        playerCounts.put(serverInfo.getName(), serverPing.getPlayers().getOnline());
                    }
                }
            });
        }
        updatePlayerCount();
        schedule(lastLength);
    }

    private void updatePlayerCount() {
        synchronized (this) {
            playerCount = 0;
            for (Integer integer : playerCounts.values()) {
                playerCount += integer;
            }
        }
    }

    public Integer getOnlineCount() {
        synchronized (this) {
            return playerCount;
        }
    }

    private synchronized void schedule(Integer length) {
        ProxyServer.getInstance().getScheduler().schedule(CoreBungeeDriver.getInstance(), this, length, TimeUnit.SECONDS);
        lastLength = length;
    }

    @Override
    public void onMessage(String s, String message) {
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(message);
        if (serverInfo == null) return;
        synchronized (ProxyServer.getInstance().getServers()) {
            ProxyServer.getInstance().getServers().remove(serverInfo.getName());
            synchronized (this) {
                playerCounts.remove(serverInfo);
            }
        }
    }

    public static ServerReaper enable() {
        ServerReaper serverReaper = new ServerReaper(ProxyServer.getInstance().getServers().values());
        serverReaper.schedule(2);
        new PubSubThread(serverReaper).start();
        return serverReaper;
    }
}
