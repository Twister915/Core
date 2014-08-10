package net.cogzmc.core.network.lilypad;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.event.EventListener;
import lilypad.client.connect.api.event.MessageEvent;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.MessageRequest;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import net.cogzmc.core.Core;
import net.cogzmc.core.network.*;
import net.cogzmc.core.network.heartbeat.HeartbeatHandler;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class LilyPadNetworkManager implements NetworkManager, HeartbeatHandler {
    /* Constants */
    private static final String NETWORK_MANAGER_CHANNEL = "CORE.LILYPAD.MANAGER";
    private static final String HEARTBEAT_PLAYERS_KEY = "PLAYERS";
    private static final String HEARTBEAT_MAX_PLAYERS_KEY = "MAX_PLAYERS";
    private static final Integer HEARTBEAT_ATTEMPTS_MAX = 5;
    static final String NET_COMMAND_CHANNEL = "CORE.LILYPAD.NETCOMMAND";

    private final List<NetworkServer> servers = new ArrayList<>();
    private final List<NetworkServerDiscoverObserver> discoverObservers = new ArrayList<>();
    @Getter private final Connect connect;
    private final Map<Class, List<NetCommandHandler>> netCommandHandlers = new HashMap<>();

    private BukkitTask heartbeatScheduled;

    public LilyPadNetworkManager() {
        connect = Core.getInstance().getServer().getServicesManager().getRegistration(Connect.class).getProvider(); //Gets the Connect plugin as per LilyPad docs.
        if (connect == null) throw new IllegalStateException("We don't have a LilyPad Connect provider");
        connect.registerEvents(this); //Register events for the messages
        LilyPadServer thisServer = new LilyPadServer(connect.getSettings().getUsername(), getMaximumPlayers(), this);
        servers.add(thisServer);
        updateThisServer();
        scheduleHeartbeat(5l, TimeUnit.SECONDS);
    }

    private Integer getMaximumPlayers() {
        return Bukkit.getServer().getMaxPlayers();
    }

    @Override
    public List<NetworkServer> getServers() {
        return new ArrayList<>(servers); //Clone because we do not want anything to modify our local copy of the servers.
    }

    @Override
    @Synchronized
    public List<NetworkServer> getServersMatchingRegex(Pattern regex) { //Sync because we want to lock this object to prevent any async updates while we are reading it.
        List<NetworkServer> networkServers = new ArrayList<>();
        for (NetworkServer server : servers) {
            if (regex.matcher(server.getName()).matches()) networkServers.add(server);
        }
        return networkServers;
    }

    @Override
    @Synchronized
    public List<NetworkServer> getServersMatchingRegex(String regex) {
        return getServersMatchingRegex(Pattern.compile(regex));
    }

    @Override
    @Synchronized
    public NetworkServer getServer(String name) {
        //Basic resolution loop
        for (NetworkServer server : this.servers) {
            if (server.getName().equals(name)) return server;
         }
        return null;
    }

    @Override
    public NetworkServer getThisServer() {
        return getServer(connect.getSettings().getUsername());
    }

    @SuppressWarnings("unchecked")
    @Override
    @Synchronized
    public void updateHeartbeat() {
        //First let's validate some of our own data here
        long time = new Date().getTime(); //Get the current date
        Iterator<NetworkServer> iterator = servers.iterator(); //Get the iterator for the servers
        while (iterator.hasNext()) {
            NetworkServer next = iterator.next();
            if (next.getName().equals(connect.getSettings().getUsername())) continue;
            if (time - next.getLastPing().getTime() > 10000) {
                iterator.remove(); //Remove servers that haven't pinged in 10 seconds
                for (NetworkServerDiscoverObserver discoverObserver : discoverObservers) {
                    discoverObserver.onNetworkServerRemove(next);
                }
            }
        }
        //If we're not connected to the cloud, don't attempt to do a heartbeat.
        if (!connect.isConnected()) {
            Core.getInstance().getLogger().severe("LILYPAD CONNECT IS NOT CONNECTED TO THE CLOUD. Unable to do a heartbeat.");
            return;
        }

        //Now we'll need to send out an encoded heartbeat, and then check if any of the servers have expired.
        JSONObject object = new JSONObject(); //Build the JSON Object
        JSONArray uuids = new JSONArray(); //Generate a JSON list of uuids
        for (CPlayer onlinePlayer : Core.getOnlinePlayers()) {
            uuids.add(onlinePlayer.getUniqueIdentifier().toString());
        }
        object.put(HEARTBEAT_PLAYERS_KEY, uuids); //and put it in the heartbeat
        object.put(HEARTBEAT_MAX_PLAYERS_KEY, getMaximumPlayers());
        MessageRequest messageRequest;
        try {
            messageRequest = new MessageRequest(Collections.EMPTY_LIST, NETWORK_MANAGER_CHANNEL, object.toJSONString());
        } catch (UnsupportedEncodingException e) {
            return;
        }
        //Now actually try to send it
        boolean completedHeartbeat = false;
        int attempts = 0;
        while (attempts < HEARTBEAT_ATTEMPTS_MAX) { //Will run until we run out of attempts!
            attempts++;
            try {
                connect.request(messageRequest);
            } catch (RequestException e) {
                e.printStackTrace();
                continue;
            }
            completedHeartbeat = true; //Mark it as completed
            break; //Break the loop since we've sent our request.
        }

        //Lastly, update this server.
        updateThisServer();
        if (!completedHeartbeat) throw new RuntimeException("Unable to send the request to do a heartbeat!");
        //Try again in four seconds.
        resetHeartbeat(4L, TimeUnit.SECONDS);
    }

    @Override
    public Integer getTotalOnlineCount() {
        int total = 0;
        for (NetworkServer server : servers) {
            total += server.getOnlineCount();
        }
        return total;
    }

    @Override
    public List<UUID> getTotalPlayersOnline() {
        List<UUID> offlinePlayers = new ArrayList<>();
        for (NetworkServer server : servers) {
            offlinePlayers.addAll(server.getPlayers());
        }
        return offlinePlayers;
    }

    @Override
    public Map<NetworkServer, Integer> getOnlinePlayersPerServer() {
        Map<NetworkServer, Integer> serverIntegerMap = new HashMap<>();
        for (NetworkServer server : servers) {
            serverIntegerMap.put(server, server.getOnlineCount());
        }
        return serverIntegerMap;
    }

    @Override
    public <T extends NetCommand> void registerNetCommandHandler(NetCommandHandler<T> handler, Class<T> type) {
        List<NetCommandHandler> netCommandHandlers1 = this.netCommandHandlers.get(type);
        if (netCommandHandlers1 == null) netCommandHandlers1 = new ArrayList<>();
        netCommandHandlers1.add(handler);
        this.netCommandHandlers.put(type, netCommandHandlers1);
    }

    @Override
    public <T extends NetCommand> void unregisterHandler(NetCommandHandler<T> handler, Class<T> type) {
        List<NetCommandHandler> netCommandHandlers1 = this.netCommandHandlers.get(type);
        if (netCommandHandlers1 == null) throw new IllegalStateException("You can't remove a handler from a type that HAS NO HANDLERS...");
        netCommandHandlers1.remove(handler);
    }

    @Override
    public <T extends NetCommand> List<NetCommandHandler<T>> getNetCommandHandlersFor(Class<T> type) {
        //Have to do this funky conversion for the generic agreement.
        List<NetCommandHandler<T>> handlers = new ArrayList<>();
        for (NetCommandHandler netCommandHandler : this.netCommandHandlers.get(type)) {
            //noinspection unchecked
            handlers.add(netCommandHandler);
        }
        return handlers;
    }

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    public void sendMassNetCommand(NetCommand command) {
        //Create a new message request, destination: Empty_List (aka all servers) on the net command channel with the text from the encodeNetCommand method.
        connect.request(new MessageRequest(Collections.EMPTY_LIST, NET_COMMAND_CHANNEL, NetworkUtils.encodeNetCommand(command).toJSONString()));
    }

    @Override
    public void registerNetworkServerDiscoverObserver(NetworkServerDiscoverObserver observer) {
        if (!discoverObservers.contains(observer)) discoverObservers.add(observer);
    }

    @Override
    public void unregisterNetworkServerDiscoverObserver(NetworkServerDiscoverObserver observer) {
        if (discoverObservers.contains(observer)) discoverObservers.remove(observer);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void handleHeartbeatData(String server, Integer maxPlayers, List<UUID> uuids) {
        LilyPadServer s;
        boolean shouldAdd = false; //Hold a marker if this is a new server...
        //Try and see if we already know this server, and if not create a new instance.
        if ((s = (LilyPadServer) getServer(server)) == null) {
            s = new LilyPadServer(server, maxPlayers, this);
            shouldAdd = true;
        }
        //Update with the heartbeat information
        s.setLastPing(new Date());
        //Now, with all the UUIDs we need to have in this list
        s.setPlayers(uuids);
        if (shouldAdd)  {
            this.servers.add(s);//And if it is a new server, add it to our servers list
            Core.logInfo("New server discovered " + s.getName() + "!");
            for (NetworkServerDiscoverObserver discoverObserver : discoverObservers) {
                discoverObserver.onNetworkServerDiscover(s);
            }
        }
    }

    private void updateThisServer() {
        LilyPadServer thisServer = (LilyPadServer) getServer(connect.getSettings().getUsername());
        List<UUID> onlinePlayers = new ArrayList<>();
        for (COfflinePlayer cPlayer : Core.getOnlinePlayers()) {
            onlinePlayers.add(cPlayer.getUniqueIdentifier());
        }
        thisServer.setPlayers(onlinePlayers);
        thisServer.setLastPing(new Date());
    }

    /* event handlers */
    @EventListener
    @Synchronized
    public void onMessage(MessageEvent event) {
        if (event.getChannel().equals(NETWORK_MANAGER_CHANNEL)) {
            handleHeartbeatMessageEvent(event); //Handle a heartbeat
            return;
        }
        if (event.getChannel().equals(NET_COMMAND_CHANNEL)) {
            handleNetCommandMessageEvent(event); //Handle a NetCommand
        }
    }

    @SneakyThrows
    private void handleHeartbeatMessageEvent(MessageEvent event) {
        if (event.getSender().equals(connect.getSettings().getUsername())) return; //If it's our heartbeat, doesn't matter either.
        try {
            String messageAsString = event.getMessageAsString();
            JSONObject heartbeat = (JSONObject)JSONValue.parse(messageAsString); //Get the values
            JSONArray playerUUIDs = (JSONArray) heartbeat.get(HEARTBEAT_PLAYERS_KEY);
            Integer maxPlayers = ((Long) (heartbeat.get(HEARTBEAT_MAX_PLAYERS_KEY))).intValue();
            List<UUID> uuids = new ArrayList<>(); //Holder for UUIDs that are converted from the strings above
            for (Object playerUUID : playerUUIDs) {
                if (!(playerUUID instanceof String)) continue;
                uuids.add(UUID.fromString(String.valueOf(playerUUID))); //Convert a string to UUID
            }
            handleHeartbeatData(event.getSender(), maxPlayers, uuids); //Update the server info.
        } catch (ClassCastException ex) {
            //Invalid heartbeat
            Core.logInfo("Unable to read heartbeat on channel due to a ClassCastException on line " + ex.getStackTrace()[0].getLineNumber());
            if (Core.DEBUG) ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void handleNetCommandMessageEvent(MessageEvent event) {
        //Get the sender
        NetworkServer sender = getServer(event.getSender());
        if (sender == null) return;
        if (Core.DEBUG) Core.logInfo(event.getMessageAsString());
        if (sender.getName().equals(connect.getSettings().getUsername())) return;
        //Get the command object (JSON) and attempt to read it
        JSONObject netCommand = (JSONObject) JSONValue.parse(event.getMessageAsString());
        NetCommand netCommand1 = NetworkUtils.decodeNetCommand(netCommand);
        //Now let's call the handlers
        List<NetCommandHandler> netCommandHandlers1 = netCommandHandlers.get(netCommand1.getClass());
        if (netCommandHandlers1 == null) return; //if there are no handlers, we don't need to do anything more.
        for (NetCommandHandler netCommandHandler : netCommandHandlers1) {
            netCommandHandler.handleNetCommand(sender, netCommand1); //"Yo dude... we got a NetCommand being sent by sender, check out netCommand1 param."
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
