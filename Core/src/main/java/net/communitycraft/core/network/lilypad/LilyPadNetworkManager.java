package net.communitycraft.core.network.lilypad;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.event.EventListener;
import lilypad.client.connect.api.event.MessageEvent;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.MessageRequest;
import lombok.Getter;
import lombok.SneakyThrows;
import net.communitycraft.core.Core;
import net.communitycraft.core.network.*;
import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

public final class LilyPadNetworkManager implements NetworkManager {
    /* Constants */
    private static final String NETWORK_MANAGER_CHANNEL = "CC.LILYPAD.MANAGER";
    private static final String HEARTBEAT_PLAYERS_KEY = "PLAYERS";
    private static final Integer HEARTBEAT_ATTEMPTS_MAX = 5;
    static final String NET_COMMAND_CHANNEL = "CC.LILYPAD.NETCOMMAND";

    private final List<NetworkServer> servers = new ArrayList<>();
    @Getter private final Connect connect;
    private final Map<Class, List<NetCommandHandler>> netCommandHandlers = new HashMap<>();

    public LilyPadNetworkManager() {
        connect = Core.getInstance().getServer().getServicesManager().getRegistration(Connect.class).getProvider(); //Gets the Connect plugin as per LilyPad docs.
        if (connect == null) throw new IllegalStateException("We don't have a LilyPad Connect provider");
        connect.registerEvents(this); //Register events for the messages
        LilyPadServer thisServer = new LilyPadServer(connect.getSettings().getUsername(), this);
        servers.add(thisServer);
        updateThisServer();
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), new NetworkUpdaterTask(this), 200L, 200L); //Setup the updater
    }

    @Override
    public List<NetworkServer> getServers() {
        return new ArrayList<>(servers); //Clone because we do not want anything to modify our local copy of the servers.
    }

    @Override
    public synchronized List<NetworkServer> getServersMatchingRegex(Pattern regex) { //Sync because we want to lock this object to prevent any async updates while we are reading it.
        List<NetworkServer> networkServers = new ArrayList<>();
        for (NetworkServer server : servers) {
            if (regex.matcher(server.getName()).matches()) networkServers.add(server);
        }
        return networkServers;
    }

    @Override
    public synchronized List<NetworkServer> getServersMatchingRegex(String regex) {
        return getServersMatchingRegex(Pattern.compile(regex));
    }

    @Override
    public synchronized NetworkServer getServer(String name) {
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
    public synchronized void updateHeartbeat() {
        //First let's validate some of our own data here
        long time = new Date().getTime(); //Get the current date
        Iterator<NetworkServer> iterator = servers.iterator(); //Get the iterator for the servers
        while (iterator.hasNext()) if (time-iterator.next().getLastPing().getTime() > 10000) iterator.remove(); //Remove servers that haven't pinged in 10 seconds

        //Now we'll need to send out an encoded heartbeat, and then check if any of the servers have expired.
        JSONObject object = new JSONObject(); //Build the JSON Object
        JSONArray uuids = new JSONArray(); //Generate a JSON list of uuids
        for (CPlayer onlinePlayer : Core.getOnlinePlayers()) {
            uuids.add(onlinePlayer.getUniqueIdentifier().toString());
        }
        object.put(HEARTBEAT_PLAYERS_KEY, uuids); //and put it in the heartbeat
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
    public List<COfflinePlayer> getTotalPlayersOnline() {
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
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
        handlers.addAll(this.netCommandHandlers.get(type));
        return handlers;
    }

    private void receivedUpdate(String server, List<UUID> uuids) {
        LilyPadServer s;
        boolean shouldAdd = false; //Hold a marker if this is a new server...
        //Try and see if we already know this server, and if not create a new instance.
        if ((s = (LilyPadServer) getServer(server)) == null) {
            s = new LilyPadServer(server, this);
            shouldAdd = true;
        }
        //Update with the heartbeat information
        s.setLastPing(new Date());
        //We need to find out which UUIDs we don't have that we need to fetch
        //Find all the UUIDs that are in the server's players
        //Remove any players that appear in the server's players that do not appear in the uuids argument
        //Add any players that appear in the uuids that do not appear in the server's players
        List<COfflinePlayer> players = s.getPlayers();
        Iterator<COfflinePlayer> playerIterator = players.iterator();
        List<UUID> uuidsWeHave = new ArrayList<>();
        while (playerIterator.hasNext()) { //So let's go through all the players we currently have stored
            COfflinePlayer offlinePlayer = playerIterator.next(); //And for each of them
            UUID uniqueIdentifier = offlinePlayer.getUniqueIdentifier(); //Get their UUID
            if (!uuids.contains(uniqueIdentifier)) playerIterator.remove(); //And check if that UUID is still valid, if not remove it
            else uuidsWeHave.add(uniqueIdentifier); //And if so, note it as something we have
        }
        //Now, with all the UUIDs we need to have in this list
        for (UUID uuid : uuids) { //We will go through them
            if (uuidsWeHave.contains(uuid)) continue; //And see if we have them
            players.add(Core.getOfflinePlayerByUUID(uuid)); //If we don't, we need to add them
        }
        if (shouldAdd) this.servers.add(s);//And if it is a new server, add it to our servers list
    }

    private void updateThisServer() {
        LilyPadServer thisServer = (LilyPadServer) getServer(connect.getSettings().getUsername());
        Collection<CPlayer> onlinePlayers = Core.getOnlinePlayers();
        ArrayList<COfflinePlayer> cPlayers = new ArrayList<>();
        cPlayers.addAll(onlinePlayers);
        thisServer.setPlayers(cPlayers);
        thisServer.setLastPing(new Date());
    }

    /* event handlers */
    @EventListener
    public synchronized void onMessage(MessageEvent event) {
        if (event.getChannel().equals(NETWORK_MANAGER_CHANNEL)) handleHeartbeatMessageEvent(event); //Handle a heartbeat
        if (event.getChannel().equals(NET_COMMAND_CHANNEL)) handleNetCommandMessageEvent(event); //Handle a NetCommand
    }

    @SneakyThrows
    private void handleHeartbeatMessageEvent(MessageEvent event) {
        if (event.getSender().equals(connect.getSettings().getUsername())) return; //If it's our heartbeat, doesn't matter either.
        try {
            String messageAsString = event.getMessageAsString();
            JSONObject heartbeat = (JSONObject)JSONValue.parse(messageAsString); //Get the values
            JSONArray playerUUIDs = (JSONArray) heartbeat.get(HEARTBEAT_PLAYERS_KEY);
            List<UUID> uuids = new ArrayList<>(); //Holder for UUIDs that are converted from the strings above
            for (Object playerUUID : playerUUIDs) {
                if (!(playerUUID instanceof String)) continue;
                uuids.add(UUID.fromString(String.valueOf(playerUUID))); //Convert a string to UUID
            }
            receivedUpdate(event.getSender(), uuids); //Update the server info.
        } catch (ClassCastException ex) {
            //Invalid heartbeat
            Core.logInfo("Unable to read heartbeat on channel due to a ClassCastException on line " + ex.getStackTrace()[0].getLineNumber());
        }
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void handleNetCommandMessageEvent(MessageEvent event) {
        //Get the sender
        NetworkServer sender = getServer(event.getSender());
        if (sender == null) return;
        //Get the command object (JSON) and attempt to read it
        JSONObject netCommand = (JSONObject) JSONValue.parse(event.getMessageAsString());
        //Get the class
        Class<? extends NetCommand> netCommandType;
        try {
            netCommandType = (Class<? extends NetCommand>) Class.forName((String) netCommand.get(LilyPadKeys.NET_COMMAND_CLASS_NAME));
        } catch (ClassNotFoundException ex) {
            return;
        }
        //Create a new instance of the NetCommand class that we found. THIS REQUIRES A NO ARGS CONSTRUCTOR TO BE PRESENT.
        NetCommand netCommand1 = netCommandType.newInstance();
        JSONObject arguments = (JSONObject)netCommand.get(LilyPadKeys.NET_COMMAND_ARGUMENTS); //Get the arguments
        boolean allFields = netCommandType.isAnnotationPresent(NetCommandField.class);
        for (Field field : netCommandType.getDeclaredFields()) { //And set the values in the class by
            if (!allFields && !field.isAnnotationPresent(NetCommandField.class)) continue; //Finding fields with this annotation
            field.setAccessible(true); //setting them accessible
            field.set(netCommand1, field.getType().cast(arguments.get(field.getName()))); //and setting their value
        }
        //Now let's call the handlers
        for (NetCommandHandler netCommandHandler : netCommandHandlers.get(netCommandType)) {
            netCommandHandler.handleNetCommand(sender, netCommand1); //"Yo dude... we got a NetCommand being sent by sender, check out netCommand1 param."
        }
    }
}
