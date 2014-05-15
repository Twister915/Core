package net.communitycraft.core.player.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.communitycraft.core.Core;
import net.communitycraft.core.asset.Asset;
import net.communitycraft.core.player.*;
import org.apache.commons.lang.IllegalClassException;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static net.communitycraft.core.RandomUtils.safeCast;
import static net.communitycraft.core.player.mongo.CMongoGroup.combineObjectBuilders;
import static net.communitycraft.core.player.mongo.CMongoGroup.getObjectForPermissible;

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

    /* Permissions */
    @Getter @Setter private ChatColor tablistColor;
    @Getter @Setter private ChatColor chatColor;
    @Getter @Setter private String chatPrefix;
    private Map<String, Boolean> declaredPermissions;
    @Getter private Map<String, Boolean> allPermissions;
    private List<CGroup> groups;

    public COfflineMongoPlayer(UUID uniqueIdentifier, DBObject player, @NonNull CMongoPlayerManager manager) {
        this.playerManager = manager;
        if (player == null) {
            this.assets = new ArrayList<>();
            this.settings = new HashMap<>();
            this.uniqueIdentifier = uniqueIdentifier;
            this.objectId = null;
            return;
        }
        this.objectId = getValueFrom(player, MongoKey.ID_KEY, ObjectId.class);
        updateFromDBObject(player);
    }

    protected COfflineMongoPlayer(COfflineMongoPlayer otherCPlayer, CMongoPlayerManager playerManager) {
        this.playerManager = playerManager;
        this.objectId = otherCPlayer.getObjectId();
        updateFromDBObject(otherCPlayer.getObjectForPlayer());
    }

    final DBObject getObjectForPlayer() {
        BasicDBObjectBuilder objectBuilder = new BasicDBObjectBuilder();
        if (this.objectId != null) objectBuilder.add(MongoKey.ID_KEY.toString(), this.objectId);
        objectBuilder.add(MongoKey.LAST_USERNAME_KEY.toString(), lastKnownUsername);
        objectBuilder.add(MongoKey.UUID_KEY.toString(), uniqueIdentifier.toString());
        objectBuilder.add(MongoKey.FIRST_JOIN_KEY.toString(), firstTimeOnline);
        objectBuilder.add(MongoKey.LAST_SEEN_KEY.toString(), lastTimeOnline);
        objectBuilder.add(MongoKey.TIME_ONLINE_KEY.toString(), millisecondsOnline);
        objectBuilder.add(MongoKey.IPS_KEY.toString(), getDBListFor(knownIPAddresses));
        objectBuilder.add(MongoKey.USERNAMES_KEY.toString(), getDBListFor(knownUsernames));
        objectBuilder.add(MongoKey.SETTINGS_KEY.toString(), getDBObjectFor(settings));
        List<Map> assetDefinition = new ArrayList<>();
        for (Asset asset : assets) {
            Map<String, Object> assetMap = new HashMap<>();
            assetMap.put(MongoKey.FULLY_QUALIFIED_CLASS_NAME_KEY.toString(), asset.getClass().getName());
            assetMap.put(MongoKey.META_KEY.toString(), asset.getMetaVariables());
            assetDefinition.add(assetMap);
        }
        objectBuilder.add(MongoKey.ASSETS_KEY.toString(), getDBListFor(assetDefinition));
        combineObjectBuilders(objectBuilder, getObjectForPermissible(this));
        return objectBuilder.get();
    }

    @Override
    public final <T> T getSettingValue(@NonNull String key, @NonNull Class<T> type, T defaultValue) {
        T t; return (this.settings.containsKey(key) || (t = safeCast(this.settings.get(key), type)) == null) ? defaultValue : t;
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

    @Override
    public void addToGroup(CGroup group) {
        this.groups.add(group);
        reloadPermissions();
    }

    @Override
    public void removeFromGroup(CGroup group) {
        this.groups.remove(group);
        reloadPermissions();
    }

    @Override
    public List<CGroup> getGroups() {
        return new ArrayList<>(groups);
    }

    private void updateFromDBObject(@NonNull DBObject player) {
        this.lastKnownUsername = getValueFrom(player, MongoKey.LAST_USERNAME_KEY, String.class);
        this.uniqueIdentifier = UUID.fromString(getValueFrom(player, MongoKey.UUID_KEY, String.class));
        this.firstTimeOnline = getValueFrom(player, MongoKey.FIRST_JOIN_KEY, Date.class);
        this.lastTimeOnline = getValueFrom(player, MongoKey.LAST_SEEN_KEY, Date.class);
        Long time_online = getValueFrom(player, MongoKey.TIME_ONLINE_KEY, Long.class);
        this.millisecondsOnline = time_online == null ? 0 : time_online;
        List<String> ips = getListFor(getValueFrom(player, MongoKey.IPS_KEY, BasicDBList.class), String.class);
        this.knownIPAddresses = ips == null ? new ArrayList<String>() : ips;
        List<String> usernames = getListFor(getValueFrom(player, MongoKey.USERNAMES_KEY, BasicDBList.class), String.class);
        this.knownUsernames = usernames == null ? new ArrayList<String>() : usernames;
        Map<String, Object> settings1 = getMapFor(getValueFrom(player, MongoKey.SETTINGS_KEY, DBObject.class));
        this.settings = settings1 == null ? new HashMap<String, Object>() : settings1;
        this.assets = new ArrayList<>();
        List<DBObject> assets1 = getListFor(getValueFrom(player,MongoKey.ASSETS_KEY , BasicDBList.class), DBObject.class);
        for (DBObject assetObject : assets1) {
            String fqcn = getValueFrom(assetObject, MongoKey.FULLY_QUALIFIED_CLASS_NAME_KEY, String.class);
            Class<?> assetClass;
            try {
                assetClass = Class.forName(fqcn);
                if (!Asset.class.isAssignableFrom(assetClass)) throw new IllegalClassException("This class does not extend Asset!");
                Map<String, Object> meta = getMapFor(getValueFrom(assetObject, MongoKey.META_KEY, DBObject.class));
                Asset asset = (Asset) assetClass.getConstructor(COfflinePlayer.class, Map.class).newInstance(this, meta);
                this.assets.add(asset);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalClassException e) {
                Core.getInstance().getLogger().severe("Could not load asset for player " + this.lastKnownUsername + " - " + fqcn + " - " + e.getMessage());
            }
        }
        CPermissible permissibileDataFor = CMongoGroup.getPermissibileDataFor(player);
        this.chatColor = permissibileDataFor.getChatColor();
        this.chatPrefix = permissibileDataFor.getChatPrefix();
        this.tablistColor = permissibileDataFor.getTablistColor();
        this.declaredPermissions = permissibileDataFor.getDeclaredPermissions();
        this.groups = new ArrayList<>();
        List<ObjectId> groupIds = getListFor(getValueFrom(player, MongoKey.USER_GROUPS_KEY, BasicDBList.class), ObjectId.class);
        for (ObjectId groupId : groupIds) {
            CGroup groupByObjectId = playerManager.getPermissionsManager().getGroupByObjectId(groupId);
            if (groupByObjectId == null) continue;
            this.groups.add(groupByObjectId);
        }
        reloadPermissions();
    }

    static <T> T getValueFrom(DBObject object, @NonNull Object key, Class<T> clazz) {
        return getValueFrom(object, key.toString(), clazz);
    }

    @SuppressWarnings("UnusedParameters")
    static <T> T getValueFrom(DBObject object, @NonNull String key, Class<T> clazz) {
        if (object == null) return null;
        try {
            //noinspection unchecked
            return (T) applyTypeFiltersForObject(object.get(key));
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
                tList.add((T)applyTypeFiltersForObject(o));
            } catch (ClassCastException ignored) {}
        }
        return tList;
    }

    static BasicDBList getDBListFor(List<?> list) {
        BasicDBList dbList = new BasicDBList();
        if (list == null) return null;
        for (Object o : list) {
            dbList.add(applyTypeFiltersForDB(o));
        }
        return dbList;
    }

    static DBObject getDBObjectFor(Map<?,?> map) {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (map == null) return null;
        for (Map.Entry<?, ?> stringEntry : map.entrySet()) {
            basicDBObject.put(stringEntry.getKey().toString(), applyTypeFiltersForDB(stringEntry.getValue()));
        }
        return basicDBObject;
    }

    static Object applyTypeFiltersForDB(Object i) {
        Object value = i;
        if (value instanceof List && !(i instanceof BasicDBList)) value = getDBListFor((List<?>) value);
        else if (value instanceof Map && !(i instanceof DBObject)) value = getDBObjectFor((Map) value);
        return value;
    }

    static Object applyTypeFiltersForObject(Object i) {
        Object value = i;
        if (i instanceof BasicDBList) value = getListFor((BasicDBList) i, Object.class);
        else if (i instanceof DBObject) value = getMapFor((DBObject) i);
        return value;
    }

    static Map<String, Object> getMapFor(DBObject object) {
        return getMapFor(object, Object.class);
    }

    @SuppressWarnings("UnusedParameters")
    static <T> Map<String, T> getMapFor(DBObject object, Class<T> valueType) {
        HashMap<String, T> map = new HashMap<>();
        if (object == null) return null;
        for (String s : object.keySet()) {
            T t;
            try {
                //noinspection unchecked
                t = (T) applyTypeFiltersForObject(object.get(s));
            } catch (ClassCastException e) {
                continue;
            }
            map.put(s, t);
        }
        return map;
    }

    @Override
    public void setPermission(String permission, Boolean value) {
        this.declaredPermissions.put(permission, value);
        reloadPermissions();
    }

    @Override
    public boolean hasPermission(String permission) {
        return allPermissions.containsKey(permission) && allPermissions.get(permission);
    }

    @Override
    public Map<String, Boolean> getDeclaredPermissions() {
        return new HashMap<>(declaredPermissions);
    }

    @Override
    public synchronized void reloadPermissions() {
        allPermissions = new HashMap<>(declaredPermissions);
        for (CGroup group : groups) {
            Map<String, Boolean> groupPermissions = group.getAllPermissions();
            for (Map.Entry<String, Boolean> permission : groupPermissions.entrySet()) {
                String permNode = permission.getKey();
                if (!allPermissions.containsKey(permNode) || !allPermissions.get(permNode)) allPermissions.put(permNode, permission.getValue());
            }
        }
    }
}
