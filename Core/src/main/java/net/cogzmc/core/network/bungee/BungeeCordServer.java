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
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.*;

@SuppressWarnings("unchecked")
@Data
@EqualsAndHashCode(of = {"name", "uuids", "lastPing", "maximumPlayers"})
final class BungeeCordServer implements NetworkServer {
    private final String name;
    private final Set<UUID> uuids = new HashSet<>();
    private final Integer maximumPlayers;
    private final BungeeCordNetworkManager networkManager;
    private Date lastPing;

    @Override
    public Integer getOnlineCount() {
        return uuids.size();
    }

    @Override
    public List<UUID> getPlayers() {
        return ImmutableList.copyOf(uuids);
    }

    @Override
    public void sendPlayerToServer(CPlayer player) {
        ByteArrayDataOutput data = ByteStreams.newDataOutput();
        data.writeUTF("Connect");
        data.writeUTF(name);
        player.getBukkitPlayer().sendPluginMessage(Core.getInstance(), "BungeeCord", data.toByteArray());
    }

    @Override
    @SneakyThrows
    public void sendNetCommand(NetCommand command) {
        JSONObject jsonObject = NetworkUtils.encodeNetCommand(command);
        JSONObject sendObject = new JSONObject();
        sendObject.put("sender", networkManager.getThisServer().getName());
        sendObject.put("net_command", jsonObject);
        Jedis resource = networkManager.getJedisPool().getResource();
        resource.publish(BungeeCordNetworkManager.NET_COMMAND_CHANNEL, sendObject.toJSONString());
        networkManager.getJedisPool().returnResource(resource);
    }
}
