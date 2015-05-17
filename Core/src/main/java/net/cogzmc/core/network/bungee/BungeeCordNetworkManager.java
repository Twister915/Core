package net.cogzmc.core.network.bungee;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Synchronized;
import lombok.ToString;
import net.cogzmc.core.Core;
import net.cogzmc.core.config.YAMLConfigurationFile;
import net.cogzmc.core.network.*;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
@EqualsAndHashCode
@ToString(of = {"servers", "thisServer", "ip"})
public class BungeeCordNetworkManager implements NetworkManager {
    static final String NET_COMMAND_CHANNEL = "CORE.BUNGEE.NETCOMMAND";
    private static final String LINK_CHANNEL = "CORE.BUNGEE.LINK";
    private static final String REAPCHANNEL = "CORE.BUNGEE.REAP";
    private static final String HEARTBEAT_CHAN = "CORE.BUNGEE.HEARTBEAT";
    static final String TELEPORT = "CORE.BUNGEE.TELEPORT";
    private static final String KICK = "CORE.BUNGEE.KICK";

    private final Map<String, BungeeCordServer> servers = new HashMap<>();
    @Getter private final BungeeCordServer thisServer;
    @Getter private final JedisPool jedisPool;

    private final Map<Class, List<NetCommandHandler>> netCommandHandlers = new HashMap<>();
    private final List<NetworkServerDiscoverObserver> discoverObservers = new ArrayList<>();

    private BukkitTask heartbeatScheduled;
    private final FileConfiguration bungeeYAML;
    private final String ip;

    public BungeeCordNetworkManager(YAMLConfigurationFile config) throws SocketException {
        bungeeYAML = config.getConfig();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(10);
        jedisPoolConfig.setMaxTotal(50);
        jedisPoolConfig.setMaxWaitMillis(100);
        jedisPoolConfig.setBlockWhenExhausted(false);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        this.jedisPool = new JedisPool(jedisPoolConfig, bungeeYAML.getString("redis.host"), bungeeYAML.getInt("redis.port"));
        this.thisServer = new BungeeCordServer(bungeeYAML.getString("name"), Bukkit.getMaxPlayers(), this);
        updateThisServer();
        new Thread(new JedisListener()).start();
        scheduleHeartbeat(5l, TimeUnit.SECONDS);
        Enumeration<InetAddress> inetAddresses = NetworkInterface.getByName(bungeeYAML.getString("network-interface")).getInetAddresses();
        InetAddress address = null;
        //noinspection StatementWithEmptyBody
        while (inetAddresses.hasMoreElements() && !(address = inetAddresses.nextElement()).getHostAddress().matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")) {
        }
        if (address == null) throw new IllegalStateException("No IP could be found!");
        ip = address.getHostAddress();
        linkServer();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Core.getInstance(), "BungeeCord");
    }

    private void linkServer() {
        Jedis resource = jedisPool.getResource();
        resource.publish(LINK_CHANNEL, "LINK;" + thisServer.getName() + ";" + ip + ":" + Bukkit.getPort());
        jedisPool.returnResource(resource);
    }

    @Override
    public List<NetworkServer> getServers() {
        ArrayList<NetworkServer> bungeeCordServers = new ArrayList<>();
        bungeeCordServers.addAll(servers.values());
        bungeeCordServers.add(thisServer);
        return ImmutableList.copyOf(bungeeCordServers);
    }

    @Override
    public List<NetworkServer> getServersMatchingRegex(Pattern regex) {
        ArrayList<NetworkServer> servers = new ArrayList<>();
        for (NetworkServer server : getServers()) {
            if (regex.matcher(server.getName()).matches()) servers.add(server);
        }
        return servers;
    }

    @Override
    public List<NetworkServer> getServersMatchingRegex(String regex) {
        return getServersMatchingRegex(Pattern.compile(regex));
    }

    @Override
    public NetworkServer getServer(String name) {
        if (thisServer.getName().equals(name)) return thisServer;
        return servers.get(name);
    }

    @Override
    @Synchronized
    public void updateHeartbeat() {
        Iterator<BungeeCordServer> iterator = servers.values().iterator();
        Long time = System.currentTimeMillis();
        while (iterator.hasNext()) {
            BungeeCordServer next = iterator.next();
            if (time - next.getLastPing().getTime() > 10000) {
                for (NetworkServerDiscoverObserver discoverObserver : discoverObservers) {
                    discoverObserver.onNetworkServerRemove(next);
                }
                iterator.remove();
            }
        }
        updateThisServer();
        String[] heartbeat = new String[3];
        heartbeat[0] = getThisServer().getName();
        heartbeat[1] = getThisServer().getPlayers().size() == 0 ? "NONE" : Joiner.on(',').join(getThisServer().getPlayers());
        heartbeat[2] = String.valueOf(getThisServer().getMaximumPlayers());
        final String join = Joiner.on(';').join(heartbeat);
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                Jedis resource = jedisPool.getResource();
                resource.publish(HEARTBEAT_CHAN, join);
                jedisPool.returnResource(resource);
            }
        });
        linkServer();
        resetHeartbeat(5L, TimeUnit.SECONDS);
    }

    private void updateThisServer() {
        thisServer.getUuids().clear();
        for (CPlayer cPlayer : Core.getOnlinePlayers()) {
            thisServer.getUuids().add(cPlayer.getUniqueIdentifier());
        }
        thisServer.setLastPing(new Date());
    }

    @Override
    public Integer getTotalOnlineCount() {
        Integer count = 0;
        for (NetworkServer networkServer : getServers()) {
            count += networkServer.getOnlineCount();
        }
        return count;
    }

    @Override
    public List<UUID> getTotalPlayersOnline() {
        List<UUID> uuids = new ArrayList<>();
        for (NetworkServer networkServer : getServers()) {
            uuids.addAll(networkServer.getPlayers());
        }
        return uuids;
    }

    @Override
    public Map<NetworkServer, Integer> getOnlinePlayersPerServer() {
        Map<NetworkServer, Integer> onlineCounts = new HashMap<>();
        for (NetworkServer networkServer : getServers()) {
            onlineCounts.put(networkServer, networkServer.getOnlineCount());
        }
        return onlineCounts;
    }

    @Override
    public <T extends NetCommand> void registerNetCommandHandler(NetCommandHandler<T> handler, Class<T> type) {
        List<NetCommandHandler> netCommandHandlers1 = netCommandHandlers.get(type);
        if (netCommandHandlers1 == null) netCommandHandlers1 = new ArrayList<>();
        netCommandHandlers1.add(handler);
        netCommandHandlers.put(type, netCommandHandlers1);
    }

    @Override
    public <T extends NetCommand> void unregisterHandler(NetCommandHandler<T> handler, Class<T> type) {
        List<NetCommandHandler> netCommandHandlers1 = netCommandHandlers.get(type);
        if (netCommandHandlers1 == null) return;
        netCommandHandlers1.remove(handler);
    }

    @Override
    public <T extends NetCommand> List<NetCommandHandler<T>> getNetCommandHandlersFor(Class<T> type) {
        List<NetCommandHandler<T>> netCommandHandlers1 = new ArrayList<>();
        for (NetCommandHandler netCommandHandler : netCommandHandlers.get(type)) {
            netCommandHandlers1.add(netCommandHandler);
        }
        return netCommandHandlers1;
    }

    @Override
    public void sendMassNetCommand(NetCommand command) {
        for (NetworkServer networkServer : getServers()) {
            networkServer.sendNetCommand(command);
        }
    }

    @Override
    public void registerNetworkServerDiscoverObserver(NetworkServerDiscoverObserver observer) {
        discoverObservers.add(observer);
    }

    @Override
    public void unregisterNetworkServerDiscoverObserver(NetworkServerDiscoverObserver observer) {
        discoverObservers.remove(observer);
    }

    @Override
    public void onDisable() {
        Jedis resource = jedisPool.getResource();
        resource.publish(LINK_CHANNEL, "UNLINK;" + getThisServer().getName());
        jedisPool.returnResource(resource);
    }

    @Override
    public boolean kickViaNetworkManager(String message, CPlayer player) {
        Jedis resource = jedisPool.getResource();
        resource.publish(KICK, player.getUniqueIdentifier().toString() + message);
        jedisPool.returnResource(resource);
        return true;
    }

    private void removeServer(NetworkServer server) {
        servers.remove(server.getName());
        for (NetworkServerDiscoverObserver discoverObserver : discoverObservers) {
            discoverObserver.onNetworkServerRemove(server);
        }
    }

    private void addServer(BungeeCordServer server) {
        servers.put(server.getName(), server);
        for (NetworkServerDiscoverObserver discoverObserver : discoverObservers) {
            discoverObserver.onNetworkServerDiscover(server);
        }
    }

    @Override
    public Iterator<NetworkServer> iterator() {
        return getServers().iterator();
    }

    @SuppressWarnings("unchecked")
    private class JedisListener extends JedisPubSub implements Runnable {
        @Override
        public void onMessage(String chan, String message) {
            Core.logDebug(chan + ":" + message + "; BG CORD");
            switch (chan) {
                case REAPCHANNEL: {
                    NetworkServer server = getServer(message);
                    if (server == null) return;
                    removeServer(server);
                    break;
                }
                case LINK_CHANNEL: {
                    if (message.equals("BUNGEE_START")) {
                        linkServer();
                        return;
                    }
                    String[] split = message.split(";");
                    if (split.length == 1) return;
                    String cmd = split[0];
                    if (cmd.equals("UNLINK")) {
                        if (split.length < 2) return;
                        NetworkServer server = getServer(split[1]);
                        if (server == null) return;
                        removeServer(server);
                    } //We use heartbeat instead of link
                    break;
                }
                case HEARTBEAT_CHAN: {
                    String[] split = message.split(";");
                    if (split.length != 3) return;
                    String name = split[0];
                    if (name.equals(thisServer.getName())) return;
                    String uuids = split[1];
                    Integer maxPlayers = Integer.parseInt(split[2]);
                    BungeeCordServer server = (BungeeCordServer) getServer(name);
                    if (server == null) {
                        server = new BungeeCordServer(name, maxPlayers, BungeeCordNetworkManager.this);
                        addServer(server);
                    }
                    server.getUuids().clear();
                    if (!uuids.equals("NONE")) {
                        for (String s : uuids.split(",")) {
                            server.getUuids().add(UUID.fromString(s));
                        }
                    }
                    server.setLastPing(new Date());
                    break;
                }
                case NET_COMMAND_CHANNEL: {
                    try {
                        JSONObject parse = (JSONObject) JSONValue.parse(message);
                        String sender = (String) parse.get("sender");
                        NetworkServer server = getServer(sender);
                        if (server == null) return;
                        JSONObject jsonObject = (JSONObject) parse.get("net_command");
                        NetCommand netCommand = NetworkUtils.decodeNetCommand(jsonObject);
                        for (NetCommandHandler netCommandHandler : netCommandHandlers.get(netCommand.getClass())) {
                            netCommandHandler.handleNetCommand(server, netCommand);
                        }
                    } catch (Exception e) {
                        Core.logDebug("Unable to gather data about NetCommand " + message);
                        if (Core.DEBUG) e.printStackTrace();
                    }
                }
            }
        }

        @Override public void onPMessage(String s, String s2, String s3) {}
        @Override public void onSubscribe(String s, int i) {}
        @Override public void onUnsubscribe(String s, int i) {}
        @Override public void onPUnsubscribe(String s, int i) {}
        @Override public void onPSubscribe(String s, int i) {}

        @Override
        public void run() {
            Jedis resource = BungeeCordNetworkManager.this.jedisPool.getResource();
            resource.subscribe(this, NET_COMMAND_CHANNEL, LINK_CHANNEL, REAPCHANNEL, HEARTBEAT_CHAN);
        }
    }

    private void scheduleHeartbeat(Long time, TimeUnit unit) {
        long ticks = unit.convert(time, TimeUnit.SECONDS) * 20;
        this.heartbeatScheduled = Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), new NetworkUpdaterTask(this), ticks, ticks);
    }

    //This will cancel any scheduled heartbeat and reschedule it for this time. Useful for doing a heartbeat at the maximum time and sending one out when people join/leave
    private void resetHeartbeat(Long time, TimeUnit unit) {
        this.heartbeatScheduled.cancel();
        scheduleHeartbeat(time, unit);
    }
}
