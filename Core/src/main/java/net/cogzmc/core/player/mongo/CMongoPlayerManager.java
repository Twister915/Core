package net.cogzmc.core.player.mongo;

import com.mongodb.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.*;
import net.cogzmc.core.player.scoreboard.ScoreboardManager;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.*;

import static net.cogzmc.core.player.mongo.MongoUtils.getValueFrom;

public final class CMongoPlayerManager extends CMongoLivePlayerRepository implements CPlayerManager {
    private CMongoDatabase database;

    private Map<String, CPlayer> onlinePlayerMap = new HashMap<>();
    private List<CPlayerConnectionListener> playerConnectionListeners = new ArrayList<>();

    @Getter private final ScoreboardManager scoreboardManager = new ScoreboardManager();

    public CMongoPlayerManager(CMongoDatabase database) {
        super(database);
        this.database = database;
        Core.getInstance().registerListener(new CPlayerManagerListener(this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), new CPlayerManagerSaveTask(this), 1200, 1200);
        DBCollection users = database.getCollection(MongoKey.USERS_COLLETION.toString());
        if (users.count() == 0) { //Looks like a new collection to me
            //Need to setup the index
            users.createIndex(new BasicDBObject(MongoKey.UUID_KEY.toString(), 1));
        }
        //Setup online player map
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                playerLoggedIn(player, player.getAddress().getAddress());
            } catch (CPlayerJoinException e) {
                e.printStackTrace();
                player.kickPlayer(ChatColor.RED + "Unable to reload player!");
            }
        }
        registerCPlayerConnectionListener(scoreboardManager);
    }

    @Override
    public List<COfflinePlayer> getOfflinePlayerByName(String username) {
        CPlayer onlinePlayer;
        if ((onlinePlayer = getOnlineCPlayerForName(username)) != null) {
            //Hmm... is there an easier way to store a single player in an array list with an implicit generic type of it's super interface?
            ArrayList<COfflinePlayer> cOfflinePlayers = new ArrayList<>();
            cOfflinePlayers.add(onlinePlayer);
            return cOfflinePlayers;
        }
        return super.getOfflinePlayerByName(username);
    }

    @Override
    public CMongoPlayer getCPlayerForPlayer(@NonNull Player player) {
        return (CMongoPlayer) this.onlinePlayerMap.get(player.getName());
    }

    @Override
    public CPlayer getOnlineCPlayerForUUID(UUID uuid) {
        return getCPlayerForPlayer(Bukkit.getPlayer(uuid));
    }

    @Override
    public CPlayer getOnlineCPlayerForName(String name) {
        return this.onlinePlayerMap.get(name);
    }

    @Override
    public List<CPlayer> getCPlayerByStartOfName(String name) {
        List<CPlayer> cPlayers = new ArrayList<>();
        String lowerCaseName = name.toLowerCase();
        for (String s : this.onlinePlayerMap.keySet()) {
            if (s.toLowerCase().startsWith(lowerCaseName)) cPlayers.add(onlinePlayerMap.get(s));
        }
        return cPlayers;
    }

    @Override
    public COfflineMongoPlayer getCOfflinePlayerForOfflinePlayer(OfflinePlayer player) {
        if (player instanceof Player) return getCPlayerForPlayer((Player) player);
        return getOfflinePlayerByUUID(player.getUniqueId());
    }

    @Override
    public Collection<CPlayer> getOnlinePlayers() {
        return this.onlinePlayerMap.values();
    }

    @Override
    @Synchronized
    public void playerLoggedIn(Player player, InetAddress address) throws CPlayerJoinException {
        //Creates a new CMongoPlayer by passing the player, the offline player (for data), and this.
        CMongoPlayer cMongoPlayer = new CMongoPlayer(player, getOfflinePlayerByUUID(player.getUniqueId()), this);
        try {
            cMongoPlayer.onJoin(address); //We attempt to notify the MongoPlayer that the player has joined on this InetAddress
        } catch (DatabaseConnectException | MongoException e) {
            //But in the instance when we cannot, we log a severe error.
            Core.getInstance().getLogger().severe("Could not read player from the database " + e.getMessage() + " - " + player.getName());
            throw new CPlayerJoinException("Error while logging you in in the CPlayerManager " + e.getClass().getSimpleName() + " : " + e.getMessage() + "\nPlease contact a developer!");
        }
        for (CPlayerConnectionListener playerConnectionListener : playerConnectionListeners) {
            playerConnectionListener.onPlayerLogin(cMongoPlayer, address);
        }
        //Now, let's place this player in our online player map
        this.onlinePlayerMap.put(player.getName(), cMongoPlayer);
        if (Core.getNetworkManager() != null) Core.getNetworkManager().updateHeartbeat(); //Send out a heartbeat.
    }

    @Override
    @Synchronized
    public void deletePlayerRecords(COfflinePlayer player) throws IllegalArgumentException {
        if (player instanceof CMongoPlayer || !(player instanceof COfflineMongoPlayer))
            throw new IllegalArgumentException("The argument you passed is not an instance of the correct object!");
        super.deletePlayerRecords(player);
    }

    @Override
    public void savePlayerData(COfflinePlayer player) throws DatabaseConnectException {
        if (player instanceof CMongoPlayer) ((CMongoPlayer)player).updateForSaving();
        super.savePlayerData(player);
    }

    @Override
    @Synchronized
    public void playerLoggedOut(Player player) {
        CMongoPlayer cPlayerForPlayer = getCPlayerForPlayer(player);
        if (cPlayerForPlayer == null) return;
        cPlayerForPlayer.updateForSaving();
        try {
            cPlayerForPlayer.saveIntoDatabase();
        } catch (DatabaseConnectException | MongoException e) {
            Core.getInstance().getLogger().severe("Could not save player into the database " + e.getMessage() + " - " + cPlayerForPlayer.getName());
        }
        for (CPlayerConnectionListener playerConnectionListener : playerConnectionListeners) {
            playerConnectionListener.onPlayerDisconnect(cPlayerForPlayer);
        }
        this.onlinePlayerMap.remove(player.getName());
        if (Core.getNetworkManager() != null) Core.getNetworkManager().updateHeartbeat();
    }

    @Override
    public void onDisable() {
        for (CPlayer onlinePlayer : getOnlinePlayers()) {
            try {
                onlinePlayer.saveIntoDatabase();
            } catch (DatabaseConnectException | MongoException e) {
                Core.getInstance().getLogger().severe("Could not save player into the database " + e.getMessage() + " - " + onlinePlayer.getName());
            }
        }
        this.database.disconnect();
    }

    @Override
    public void registerCPlayerConnectionListener(CPlayerConnectionListener processor) {
        if (this.playerConnectionListeners.contains(processor)) return;
        this.playerConnectionListeners.add(processor);
    }

    @Override
    public void unregisterCPlayerConnectionListener(CPlayerConnectionListener processor) {
        if (!this.playerConnectionListeners.contains(processor)) return;
        this.playerConnectionListeners.remove(processor);
    }

    @Override
    public Iterator<CPlayer> iterator() {
        //This needs to get all the online players as an iterator.
        return this.onlinePlayerMap.values().iterator();
    }
}
