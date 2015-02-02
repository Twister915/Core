package net.cogzmc.core.network.bungee;

import net.cogzmc.core.Core;
import net.cogzmc.core.network.NetCommand;
import net.cogzmc.core.network.NetworkServer;
import net.cogzmc.core.network.NetworkUtils;
import net.cogzmc.core.player.CPlayer;

import java.util.*;

@SuppressWarnings("unchecked")
@Data
@EqualsAndHashCode(of = {"name", "uuids", "lastPing", "maximumPlayers"})
public class BungeeCordServer implements NetworkServer {
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
    @SneakyThrows
    public void sendNetCommand(NetCommand command) {
        JSONObject jsonObject = NetworkUtils.encodeNetCommand(command);
        final JSONObject sendObject = new JSONObject();
        sendObject.put("sender", networkManager.getThisServer().getName());
        sendObject.put("net_command", jsonObject);
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                Jedis resource = networkManager.getJedisPool().getResource();
                resource.publish(BungeeCordNetworkManager.NET_COMMAND_CHANNEL, sendObject.toJSONString());
                networkManager.getJedisPool().returnResource(resource);
            }
        });
    }
}
