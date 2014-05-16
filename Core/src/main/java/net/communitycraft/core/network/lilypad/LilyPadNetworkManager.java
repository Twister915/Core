package net.communitycraft.core.network.lilypad;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.event.EventListener;
import lilypad.client.connect.api.event.MessageEvent;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.MessageRequest;
import lombok.Getter;
import lombok.SneakyThrows;
import net.communitycraft.core.Core;
import net.communitycraft.core.network.NetworkUpdaterTask;
import net.communitycraft.core.player.CPlayer;
import net.communitycraft.core.network.NetworkManager;
import net.communitycraft.core.network.NetworkServer;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

public final class LilyPadNetworkManager implements NetworkManager {
    /* Constants */
    private static final String NETWORK_MANAGER_CHANNEL = "CC.LILYPAD.MANAGER";
    private static final String HEARTBEAT_PLAYERS_KEY = "PLAYERS";
    private static final Integer HEARTBEAT_ATTEMPTS_MAX = 5;


    private final List<NetworkServer> servers = new ArrayList<>();
    @Getter private final Connect connect;

    public LilyPadNetworkManager() {
        connect = Core.getInstance().getServer().getServicesManager().getRegistration(Connect.class).getProvider(); //Gets the Connect plugin as per LilyPad docs.
        if (connect == null) throw new IllegalStateException("We don't have a LilyPad Connect provider");
        connect.registerEvents(this); //Register events for the messages
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), new NetworkUpdaterTask(this), 40L, 40L); //Setup the updater
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

        if (!completedHeartbeat) throw new RuntimeException("Unable to send the request to do a heartbeat!");
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
        s.setOnlineCount(uuids.size());
        s.setLastPing(new Date());
        s.setPlayers(Core.getPlayerManager().getOfflinePlayersByUUIDS(uuids));
        if (shouldAdd) this.servers.add(s);//And if it is a new server, add it to our servers list
    }

    /* event handlers */
    @EventListener
    @SneakyThrows
    public synchronized void onMessage(MessageEvent event) {
        if (!event.getChannel().equals(NETWORK_MANAGER_CHANNEL)) return; //If it's not our channel, ignore this
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
}
