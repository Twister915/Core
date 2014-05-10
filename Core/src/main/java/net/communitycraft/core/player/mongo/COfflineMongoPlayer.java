package net.communitycraft.core.player.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.communitycraft.core.Core;
import net.communitycraft.core.asset.Asset;
import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPlayer;
import net.communitycraft.core.player.DatabaseConnectException;
import org.apache.commons.lang.IllegalClassException;
import org.bson.types.ObjectId;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

class COfflineMongoPlayer implements COfflinePlayer {
    @Getter private List<String> knownUsernames;
    @Getter @Setter(AccessLevel.PROTECTED) private String lastKnownUsername;
    @Getter private UUID uniqueIdentifier;
    @Getter private List<String> knownIPAddresses;
    @Getter @Setter(AccessLevel.PROTECTED) private Date firstTimeOnline;
    @Getter @Setter(AccessLevel.PROTECTED) private Date lastTimeOnline;
    @Getter @Setter(AccessLevel.PROTECTED) private Long millisecondsOnline;

    @Getter private List<Asset> assets;
    private Map<String, Object> settings;

    /* helpers */
    private final CMongoPlayerManager playerManager;
    @Getter @Setter private ObjectId objectId;

    public COfflineMongoPlayer(UUID uniqueIdentifier, DBObject player, @NonNull CMongoPlayerManager manager) {
        this.playerManager = manager;
        if (player == null) {
            this.assets = new ArrayList<>();
            this.settings = new HashMap<>();
            this.uniqueIdentifier = uniqueIdentifier;
            this.objectId = null;
            return;
        }
        this.objectId = getValueFrom(player, "_id", ObjectId.class);
        updateFromDBObject(player);
    }

    protected COfflineMongoPlayer(COfflineMongoPlayer otherCPlayer, CMongoPlayerManager playerManager) {
        this.playerManager = playerManager;
        this.objectId = otherCPlayer.getObjectId();
        updateFromDBObject(otherCPlayer.getObjectForPlayer());
    }

    final DBObject getObjectForPlayer() {
        DBObject object = new BasicDBObject();
        if (this.objectId != null) object.put("_id", this.objectId);
        object.put("last_username", lastKnownUsername);
        object.put("uuid", uniqueIdentifier.toString());
        object.put("first_join", firstTimeOnline);
        object.put("last_seen", lastTimeOnline);
        object.put("time_online", millisecondsOnline);
        object.put("ips", getDBListFor(knownIPAddresses));
        object.put("usernames", getDBListFor(knownUsernames));
        object.put("settings", getDBObjectFor(settings));
        List<BasicDBObject> assetDefinition = new ArrayList<>();
        for (Asset asset : assets) {
            BasicDBObject assetMap = new BasicDBObject();
            assetMap.put("fqcn", asset.getClass().getName());
            assetMap.put("meta", getDBObjectFor(asset.getMetaVariables()));
            assetDefinition.add(assetMap);
        }
        object.put("assets", getDBListFor(assetDefinition));
        return object;
    }

    @Override
    public final <T> T getSettingValue(@NonNull String key, @NonNull Class<T> type, T defaultValue) {
        T t;
        try {
            //noinspection unchecked
            t = (T) this.settings.get(key);
        } catch (ClassCastException ex) {
            t = null;
        }
        return t == null ? defaultValue : t;
    }

    @Override
    public final <T> T getSettingValue(@NonNull String key, @NonNull Class<T> type) {
        return getSettingValue(key, type, null);
    }

    @Override
    public final void storeSettingValue(@NonNull String key, Object value) {
        this.settings.put(key, value);
    }

    @Override
    public final void removeSettingValue(@NonNull String key) {
        this.settings.remove(key);
    }

    @Override
    public final boolean containsSetting(@NonNull String key) {
        return this.settings.containsKey(key);
    }

    @Override
    public final void giveAsset(@NonNull Asset asset) {
        this.assets.add(asset);
    }

    @Override
    public final void removeAsset(@NonNull Asset asset) {
        this.assets.remove(asset);
    }

    @Override
    public final CPlayer getPlayer() {
        return this.playerManager.getOnlineCPlayerForUUID(this.uniqueIdentifier);
    }

    @Override
    public final void updateFromDatabase() {
        updateFromDBObject(this.playerManager.getPlayerDocumentFor(this.uniqueIdentifier));
    }

    @Override
    public final void saveIntoDatabase() throws DatabaseConnectException {
        this.playerManager.savePlayerData(this);
    }

    private void updateFromDBObject(@NonNull DBObject player) {
        this.lastKnownUsername = getValueFrom(player, "last_username", String.class);
        this.uniqueIdentifier = UUID.fromString(getValueFrom(player, "uuid", String.class));
        this.firstTimeOnline = getValueFrom(player, "first_join", Date.class);
        this.lastTimeOnline = getValueFrom(player, "last_seen", Date.class);
        Long time_online = getValueFrom(player, "time_online", Long.class);
        this.millisecondsOnline = time_online == null ? 0 : time_online;
        List<String> ips = getListFor(getValueFrom(player, "ips", BasicDBList.class), String.class);
        this.knownIPAddresses = ips == null ? new ArrayList<String>() : ips;
        List<String> usernames = getListFor(getValueFrom(player, "usernames", BasicDBList.class), String.class);
        this.knownUsernames = usernames == null ? new ArrayList<String>() : usernames;
        Map<String, Object> settings1 = getMapFor(getValueFrom(player, "settings", DBObject.class));
        this.settings = settings1 == null ? new HashMap<String, Object>() : settings1;
        this.assets = new ArrayList<>();
        List<DBObject> assets1 = getListFor(getValueFrom(player, "assets", BasicDBList.class), DBObject.class);
        for (DBObject assetObject : assets1) {
            String fqcn = getValueFrom(assetObject, "fqcn", String.class);
            Class<?> assetClass;
            try {
                assetClass = Class.forName(fqcn);
                if (!Asset.class.isAssignableFrom(assetClass)) throw new IllegalClassException("This class does not extend Asset!");
                Map<String, Object> meta = getMapFor(getValueFrom(assetObject, "meta", DBObject.class));
                Asset asset = (Asset) assetClass.getConstructor(COfflinePlayer.class, Map.class).newInstance(this, meta);
                this.assets.add(asset);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalClassException e) {
                Core.getInstance().getLogger().severe("Could not load asset for player " + this.lastKnownUsername + " - " + fqcn + " - " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("UnusedParameters")
    static <T> T getValueFrom(DBObject object, @NonNull String key, Class<T> clazz) {
        if (object == null) return null;
        try {
            //noinspection unchecked
            return (T)object.get(key);
        } catch (ClassCastException ex) {
            return null;
        }
    }

    @SuppressWarnings("UnusedParameters")
    static <T> List<T> getListFor(BasicDBList list, Class<T> clazz) {
        List<T> tList = new ArrayList<>();
        if (list == null) return null;
        for (Object o : list) {
            try {
                //noinspection unchecked
                tList.add((T) o);
            } catch (ClassCastException ignored) {}
        }
        return tList;
    }

    static BasicDBList getDBListFor(List<?> list) {
        BasicDBList dbList = new BasicDBList();
        if (list == null) return null;
        for (Object o : list) {
            dbList.add(o);
        }
        return dbList;
    }

    static DBObject getDBObjectFor(Map<String, ?> map) {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (map == null) return null;
        for (Map.Entry<String, ?> stringEntry : map.entrySet()) {
            basicDBObject.put(stringEntry.getKey(), stringEntry.getValue());
        }
        return basicDBObject;
    }

    static Map<String, Object> getMapFor(DBObject object) {
        HashMap<String, Object> map = new HashMap<>();
        if (object == null) return null;
        for (String s : object.keySet()) {
            map.put(s, object.get(s));
        }
        return map;
    }
}
