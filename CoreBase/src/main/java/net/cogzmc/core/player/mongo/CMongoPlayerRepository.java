package net.cogzmc.core.player.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lombok.Data;
import lombok.Synchronized;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayerRepository;
import net.cogzmc.core.player.DatabaseConnectException;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.cogzmc.core.player.mongo.MongoUtils.getValueFrom;

@Data
public class CMongoPlayerRepository implements CPlayerRepository {
    protected final CMongoDatabase database;

    @Override
    public List<COfflinePlayer> getOfflinePlayerByName(String username) {
        DBCollection collection = database.getCollection(MongoKey.USERS_COLLETION.toString());
        DBObject one = collection.findOne(new BasicDBObject(MongoKey.LAST_USERNAME_KEY.toString(), username));
        if (one != null) return Arrays.asList(playerFrom(one));
        DBCursor dbObjects = collection.find(new BasicDBObject(MongoKey.USERNAMES_KEY.toString(), username));
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
        for (DBObject dbObject : dbObjects) {
            offlinePlayers.add(playerFrom(dbObject));
        }
        return offlinePlayers;
    }

    COfflinePlayer playerFrom(DBObject dbObject) {
        return new COfflineMongoPlayer(UUID.fromString(getValueFrom(dbObject, MongoKey.UUID_KEY.toString(), String.class)), dbObject, this);
    }

    COfflineMongoPlayer getPlayerFor(UUID uuid, DBObject object) {
        return new COfflineMongoPlayer(uuid, object, this);
    }

    DBObject getPlayerDocumentFor(UUID uuid) {
        //gets the users collection                                        finds something matching  UUID                     =    the param represented as a string
        return database.getCollection(MongoKey.USERS_COLLETION.toString()).findOne(new BasicDBObject(MongoKey.UUID_KEY.toString(), uuid.toString()));
    }

    @Override
    public COfflineMongoPlayer getOfflinePlayerByUUID(UUID uuid) {
        DBObject playerDocumentFor = getPlayerDocumentFor(uuid);
        //We perform no null check here on purpose. The playerDocumentFor variable, when null, is checked in the constructor and used as a marker for a new player
        return getPlayerFor(uuid, playerDocumentFor);
    }

    @Override
    public List<COfflinePlayer> getOfflinePlayersByUUIDS(List<UUID> uuids) {
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
        for (UUID uuid : uuids) {
            DBObject playerDocumentFor = getPlayerDocumentFor(uuid);
            if (playerDocumentFor == null) continue; //If this UUID is invalid, this method will not return the player.
            //TODO actually, this is just here to mark this as a point of interest. Should we create new players we can't find a match for or should we ignore them?
            offlinePlayers.add(getPlayerFor(uuid, playerDocumentFor));
        }
        return offlinePlayers;
    }

    @Override
    public void savePlayerData(COfflinePlayer player) throws DatabaseConnectException {
        //Gets the COfflineMongoPlayer
        COfflineMongoPlayer player1 = (COfflineMongoPlayer) player;
        //And then get the database object representation.
        DBObject objectForPlayer = player1.getObjectForPlayer();
        //And save it into the database.
        this.database.getCollection(MongoKey.USERS_COLLETION.toString()).save(objectForPlayer);
        player1.setObjectId(getValueFrom(objectForPlayer, MongoKey.ID_KEY, ObjectId.class));
    }

    @Override
    @Synchronized
    public void deletePlayerRecords(COfflinePlayer player) throws IllegalArgumentException {
        database.getCollection(MongoKey.USERS_COLLETION.toString()).remove(new BasicDBObject(MongoKey.ID_KEY.toString(), ((COfflineMongoPlayer) player).getObjectId()));
    }

    public COfflinePlayer getOfflinePlayerByObjectId(ObjectId id) {
        //Find the player doc by the ID from the users collection
        DBObject one = database.getCollection(MongoKey.USERS_COLLETION.toString()).findOne(new BasicDBObject(MongoKey.ID_KEY.toString(), id));
        if (one == null) return null;
        UUID uuid = UUID.fromString(getValueFrom(one, MongoKey.UUID_KEY, String.class)); //Get the UUID from that doc, and
        return new COfflineMongoPlayer(uuid, one, this); //Create a new COfflineMongoPlayer with that.
    }
}
