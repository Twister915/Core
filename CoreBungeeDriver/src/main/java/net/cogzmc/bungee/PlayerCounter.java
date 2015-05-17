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
public final class PlayerCounter {
    private Integer playerCount = 0;
    private Integer lastLength = 0;

    volatile Map<String, Integer> playerCounts = new HashMap<>();

    public Integer getOnlineCount() {
        Integer sum = 0;
        for (Integer integer : playerCounts.values()) {
            sum += integer;
        }
        return sum;
    }
}
