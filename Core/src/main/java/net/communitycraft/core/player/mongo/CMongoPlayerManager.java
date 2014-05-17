package net.communitycraft.core.player.mongo;

import com.mongodb.*;
import lombok.Getter;
import lombok.NonNull;
import net.communitycraft.core.Core;
import net.communitycraft.core.player.*;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.*;

import static net.communitycraft.core.player.mongo.MongoUtils.getValueFrom;

public final class CMongoPlayerManager implements CPlayerManager {
    @Getter private CMongoDatabase database;
    @Getter private CMongoPermissionsManager permissionsManager;

    private Map<String, CPlayer> onlinePlayerMap = new HashMap<>();

    public CMongoPlayerManager(CMongoDatabase database) throws DatabaseConnectException {
        this.database = database;
        database.connect();
        this.permissionsManager = new CMongoPermissionsManager(database, this);
        Core.getInstance().registerListener(new CPlayerManagerListener(this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), new CPlayerManagerSaveTask(this), 1200, 1200);
        DBCollection users = database.getCollection(MongoKey.USERS_COLLETION.toString());
        if (users.count() == 0) { //Looks like a new collection to me
            //Need to setup the index
            users.createIndex(new BasicDBObject(MongoKey.UUID_KEY.toString(), 1));
        }
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
        DBCursor dbObjects = database.getCollection(MongoKey.USERS_COLLETION.toString()).find(new BasicDBObject(MongoKey.USERNAMES_KEY.toString(), username));
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
        for (DBObject dbObject : dbObjects) {
            offlinePlayers.add(new COfflineMongoPlayer(UUID.fromString(getValueFrom(dbObject, MongoKey.UUID_KEY.toString(), String.class)), dbObject, this));
        }
        return offlinePlayers;
    }

    @Override
    public COfflineMongoPlayer getOfflinePlayerByUUID(UUID uuid) {
        DBObject playerDocumentFor = getPlayerDocumentFor(uuid);
        //We perform no null check here on purpose. The playerDocumentFor variable, when null, is checked in the constructor and used as a marker for a new player
        return new COfflineMongoPlayer(uuid, playerDocumentFor, this);
    }

    @Override
    public List<COfflinePlayer> getOfflinePlayersByUUIDS(List<UUID> uuids) {
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
        for (UUID uuid : uuids) {
            DBObject playerDocumentFor = getPlayerDocumentFor(uuid);
            if (playerDocumentFor == null) continue; //If this UUID is invalid, this method will not return the player.
            //TODO actually, this is just here to mark this as a point of interest. Should we create new players we can't find a match for or should we ignore them?
            offlinePlayers.add(new COfflineMongoPlayer(uuid, playerDocumentFor, this));
        }
        return offlinePlayers;
    }

    public COfflinePlayer getOfflinePlayerByObjectId(ObjectId id) {
        //Find the player doc by the ID from the users collection
        DBObject one = database.getCollection(MongoKey.USERS_COLLETION.toString()).findOne(new BasicDBObject(MongoKey.ID_KEY.toString(), id));
        if (one == null) return null;
        UUID uuid = UUID.fromString(getValueFrom(one, MongoKey.UUID_KEY, String.class)); //Get the UUID from that doc, and
        return new COfflineMongoPlayer(uuid, one, this); //Create a new COfflineMongoPlayer with that.
    }

    DBObject getPlayerDocumentFor(UUID uuid) {
        //gets the users collection                                        finds something matching  UUID                     =    the param represented as a string
        return database.getCollection(MongoKey.USERS_COLLETION.toString()).findOne(new BasicDBObject(MongoKey.UUID_KEY.toString(), uuid.toString()));
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
    public COfflineMongoPlayer getCOfflinePlayerForOfflinePlayer(OfflinePlayer player) {
        return getOfflinePlayerByUUID(player.getUniqueId());
    }

    @Override
    public Collection<CPlayer> getOnlinePlayers() {
        return this.onlinePlayerMap.values();
    }

    @Override
    public void savePlayerData(COfflinePlayer player) throws DatabaseConnectException {
        //Gets the COfflineMongoPlayer
        COfflineMongoPlayer player1 = (COfflineMongoPlayer) player;
        //Attempts to update data if this player is currently online (in which case it would be an instance of CMongoPlayer)
        if (player1 instanceof CMongoPlayer) ((CMongoPlayer)player1).updateForSaving();
        //And then get the database object representation.
        DBObject objectForPlayer = player1.getObjectForPlayer();
        //And save it into the database.
        this.database.getCollection(MongoKey.USERS_COLLETION.toString()).save(objectForPlayer);
        player1.setObjectId(getValueFrom(objectForPlayer, MongoKey.ID_KEY, ObjectId.class));
    }

    @Override
    public void playerLoggedIn(Player player, InetAddress address) {
        //Creates a new CMongoPlayer by passing the player, the offline player (for data), and this.
        CMongoPlayer cMongoPlayer = new CMongoPlayer(player, getOfflinePlayerByUUID(player.getUniqueId()), this);
        try {
            cMongoPlayer.onJoin(address); //We attempt to notify the MongoPlayer that the player has joined on this InetAddress
        } catch (DatabaseConnectException | MongoException e) {
            //But in the instance when we cannot, we log a severe error.
            Core.getInstance().getLogger().severe("Could not read player from the database " + e.getMessage() + " - " + player.getName());
            player.kickPlayer("Error while logging you in in the CPlayerManager " + e.getClass().getSimpleName() + " : " + e.getMessage() + "\nPlease contact a developer!");
            return;
        }
        Core.getNetworkManager().updateHeartbeat(); //Send out a heartbeat.
        //Now, let's place this player in our online player map
        this.onlinePlayerMap.put(player.getName(), cMongoPlayer);
    }

    @Override
    public void playerLoggedOut(Player player) {
        CMongoPlayer cPlayerForPlayer = getCPlayerForPlayer(player);
        if (cPlayerForPlayer == null) return;
        cPlayerForPlayer.updateForSaving();
        try {
            cPlayerForPlayer.saveIntoDatabase();
        } catch (DatabaseConnectException | MongoException e) {
            Core.getInstance().getLogger().severe("Could not save player into the database " + e.getMessage() + " - " + cPlayerForPlayer.getName());
        }
        this.onlinePlayerMap.remove(player.getName());
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
    public Iterator<CPlayer> iterator() {
        //This needs to get all the online players as an iterator.
        return this.onlinePlayerMap.values().iterator();
    }
}
