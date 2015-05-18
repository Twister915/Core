package net.cogzmc.core.network.bungee;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.*;
import net.cogzmc.core.Core;
import net.cogzmc.core.network.NetCommand;
import net.cogzmc.core.network.NetworkServer;
import net.cogzmc.core.network.NetworkUtils;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.*;

@SuppressWarnings("unchecked")
@Data
@ToString(of = {"name", "uuids", "lastPing", "maximumPlayers"})
@EqualsAndHashCode(of = {"name", "uuids", "lastPing", "maximumPlayers"})
public class BungeeCordServer implements NetworkServer {
    private final String name;
    private final Set<UUID> uuids = new HashSet<>();
    private final Integer maximumPlayers;
    private final BungeeCordNetworkManager networkManager;
    private Date lastPing;

    @Override
    public String getName() {
        return name;
    }

    @Override
    @Synchronized
    public Integer getOnlineCount() {
        return uuids.size();
    }

    @Override
    public Integer getMaximumPlayers() {
        return maximumPlayers;
    }

    @Override
    @Synchronized
    public List<UUID> getPlayers() {
        return ImmutableList.copyOf(uuids);
    }

    @Override
    @Synchronized
    public void sendPlayerToServer(final CPlayer player) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                Jedis resource = networkManager.getJedisPool().getResource();
                resource.publish(BungeeCordNetworkManager.TELEPORT, player.getUniqueIdentifier() + "|" + name);
                resource.close();
            }
        });
    }

    @Override
    @Synchronized
    public Date getLastPing() {
        return lastPing;
    }

    @Override
    @SneakyThrows
    @Synchronized
    public void sendNetCommand(NetCommand command) {
        JSONObject jsonObject = NetworkUtils.encodeNetCommand(command);
        final JSONObject sendObject = new JSONObject();
        sendObject.put("sender", networkManager.getThisServer().getName());
        sendObject.put("net_command", jsonObject);
        sendObject.put("dest", name);
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                Jedis resource = networkManager.getJedisPool().getResource();
                resource.publish(BungeeCordNetworkManager.NET_COMMAND_CHANNEL, sendObject.toJSONString());
                networkManager.getJedisPool().returnResource(resource);
            }
        });
    }

    @Synchronized
    public Set<UUID> getUuids() {
        return uuids;
    }

    @Synchronized
    public void setLastPing(Date lastPing) {
        this.lastPing = lastPing;
    }
}
