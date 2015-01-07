package net.cogzmc.core.player.mongo;

import com.google.common.base.Joiner;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import lombok.*;
import lombok.extern.java.Log;
import net.cogzmc.core.asset.Asset;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPermissible;
import net.cogzmc.core.player.DatabaseConnectException;
import net.cogzmc.util.ColorSupplements;
import org.bson.types.ObjectId;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.*;

import static net.cogzmc.core.player.mongo.MongoUtils.*;
import static net.cogzmc.util.RandomUtils.safeCast;

@EqualsAndHashCode(of = {"uniqueIdentifier"})
@Log
public class COfflineMongoPlayer implements COfflinePlayer {
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
    protected final CMongoPlayerRepository playerRepository;
    @Getter @Setter private ObjectId objectId;

    /* Permissions */
    @Getter @Setter private String tablistColor;
    @Getter @Setter private String chatColor;
    @Getter @Setter private String chatPrefix;
    @Getter @Setter private String chatSuffix;
    private String displayName;
    protected Map<String, Boolean> declaredPermissions;
    @Getter protected Map<String, Boolean> allPermissions;
    protected List<CGroup> groups;
    @Getter protected CGroup primaryGroup;
    protected List<ObjectId> groupIds;

    //Called in all instances when we're loading a player from the database
    public COfflineMongoPlayer(UUID uniqueIdentifier, DBObject player, @NonNull CMongoPlayerRepository repository) {
        this.playerRepository = repository;
        if (player == null) {
            this.assets = new ArrayList<>();
            this.settings = new HashMap<>();
            this.uniqueIdentifier = uniqueIdentifier;
            this.objectId = null;
            this.declaredPermissions = new HashMap<>();
            this.groups = new ArrayList<>();
            this.groupIds = new ArrayList<>();
            return;
        }
        this.objectId = getValueFrom(player, MongoKey.ID_KEY, ObjectId.class);
        updateFromDBObject(player); //Updates the states of our variables using the database object.
    }

    //Used as the super-constructor when we're creating a CPlayer from a COfflinePlayer (when a player comes online)
    //This copies the state of all variables in the other COfflinePlayer object that is being passed.
    protected COfflineMongoPlayer(COfflineMongoPlayer otherCPlayer, CMongoPlayerRepository manager) {
        this.playerRepository = manager;
        this.objectId = otherCPlayer.getObjectId();
        updateFromDBObject(otherCPlayer.getObjectForPlayer());
    }

    @Synchronized
    final DBObject getObjectForPlayer() {
        BasicDBObjectBuilder objectBuilder = new BasicDBObjectBuilder();
        if (this.objectId != null) objectBuilder.add(MongoKey.ID_KEY.toString(), this.objectId);
        objectBuilder.add(MongoKey.LAST_USERNAME_KEY.toString(), lastKnownUsername);
        objectBuilder.add(MongoKey.UUID_KEY.toString(), uniqueIdentifier.toString());
        objectBuilder.add(MongoKey.DISPLAY_NAME.toString(), displayName);
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
        List<ObjectId> groupIds = new ArrayList<>();
        groupIds.addAll(this.groupIds);
        objectBuilder.add(MongoKey.USER_GROUPS_KEY.toString(), getDBListFor(groupIds));
        combineObjectBuilders(objectBuilder, getObjectForPermissible(this));
        return objectBuilder.get();
    }

    @Override
    public Set<String> getSettingKeys() {
        return settings.keySet();
    }

    @Override
    public final <T> T getSettingValue(@NonNull String key, @NonNull Class<T> type, T defaultValue) {
        T t; return (!this.settings.containsKey(key) || (t = safeCast(this.settings.get(key), type)) == null) ? defaultValue : t;
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
    public final void updateFromDatabase() {
        updateFromDBObject(this.playerRepository.getPlayerDocumentFor(this.uniqueIdentifier));
    }

    @Override
    public final void saveIntoDatabase() throws DatabaseConnectException {
        this.playerRepository.savePlayerData(this);
    }

    @Override
    public void addToGroup(CGroup group) {
        if (this.groups.contains(group)) throw new IllegalArgumentException("This player is already a member of this group!");
        this.groupIds.add(((CMongoGroup) group).getObjectId());
        reloadPermissions();
    }

    @Override
    public void removeFromGroup(CGroup group) {
        if (!this.groups.contains(group)) throw new IllegalArgumentException("This player is not a member of this group!");
        this.groupIds.remove(((CMongoGroup)group).getObjectId());
        reloadPermissions();
    }

    @Override
    public List<CGroup> getGroups() {
        return new ArrayList<>(groups);
    }

    protected void updateFromDBObject(@NonNull DBObject player) {
        this.lastKnownUsername = getValueFrom(player, MongoKey.LAST_USERNAME_KEY, String.class);
        this.uniqueIdentifier = UUID.fromString(getValueFrom(player, MongoKey.UUID_KEY, String.class));
        this.displayName = getValueFrom(player, MongoKey.DISPLAY_NAME, String.class);
        if (this.displayName != null) this.displayName = ColorSupplements.translateAlternateColorCodes('&', this.displayName);
        else this.displayName = null;
        this.firstTimeOnline = getValueFrom(player, MongoKey.FIRST_JOIN_KEY, Date.class);
        this.lastTimeOnline = getValueFrom(player, MongoKey.LAST_SEEN_KEY, Date.class);
        Long time_online = getValueFrom(player, MongoKey.TIME_ONLINE_KEY, Long.class);
        this.millisecondsOnline = time_online == null ? 0 : time_online;
        List<String> ips = getListFor(getValueFrom(player, MongoKey.IPS_KEY, BasicDBList.class), String.class);
        this.knownIPAddresses = ips == null ? new ArrayList<String>() : ips;
        List<String> usernames = getListFor(getValueFrom(player, MongoKey.USERNAMES_KEY, BasicDBList.class), String.class);
        this.knownUsernames = usernames == null ? new ArrayList<String>() : usernames;
        @SuppressWarnings("unchecked") Map<String, Object> settings1 = getMapFor(getValueFrom(player, MongoKey.SETTINGS_KEY, BasicDBObject.class));
        this.settings = settings1 == null ? new HashMap<String, Object>() : settings1;
        this.assets = new ArrayList<>();
        List<DBObject> assets1 = getListFor(getValueFrom(player,MongoKey.ASSETS_KEY , BasicDBList.class), DBObject.class);
        for (DBObject assetObject : assets1) {
            String fqcn = getValueFrom(assetObject, MongoKey.FULLY_QUALIFIED_CLASS_NAME_KEY, String.class);
            Class<?> assetClass;
            try {
                assetClass = Class.forName(fqcn);
                if (!Asset.class.isAssignableFrom(assetClass)) throw new ClassNotFoundException("This class does not extend Asset!");
                Map<String, Object> meta = getMapFor(getValueFrom(assetObject, MongoKey.META_KEY, DBObject.class));
                Asset asset = (Asset) assetClass.getConstructor(COfflinePlayer.class, Map.class).newInstance(this, meta);
                this.assets.add(asset);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                log.severe("Could not load asset for player " + this.lastKnownUsername + " - " + fqcn + " - " + e.getMessage());
            }
        }
        CPermissible permissibleDataFor = getPermissibileDataFor(player);
        this.chatColor = permissibleDataFor.getChatColor();
        this.chatPrefix = permissibleDataFor.getChatPrefix();
        this.chatSuffix = permissibleDataFor.getChatSuffix();
        this.tablistColor = permissibleDataFor.getTablistColor();
        this.declaredPermissions = permissibleDataFor.getDeclaredPermissions();
        if (this.declaredPermissions == null) this.declaredPermissions = new HashMap<>();
        groupIds = getListFor(getValueFrom(player, MongoKey.USER_GROUPS_KEY, BasicDBList.class), ObjectId.class);
        reloadPermissions0();
    }

    @Override
    public void setPermission(String permission, Boolean value) {
        this.declaredPermissions.put(permission, value);
        reloadPermissions();
    }

    @Override
    public void unsetPermission(String permission) {
        this.declaredPermissions.remove(permission);
        reloadPermissions();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (allPermissions.containsKey(permission) && allPermissions.get(permission))
            return true;
        //wildcard support
        String[] parts = permission.split(".");
        return parts.length > 1 && hasPermission(Joiner.on('.').join(Arrays.copyOfRange(parts, 0, parts.length - 2)) + ".*");
    }

    @Override
    public boolean isSet(String permission) {
        return this.declaredPermissions.containsKey(permission);
    }

    @Override
    public Map<String, Boolean> getDeclaredPermissions() {
        return new HashMap<>(declaredPermissions);
    }

    //This is overridden in the subclass, and as such we do not want to call this method directly from within our class. That would trigger the subclass in times we do not want to.
    @Override
    @Synchronized
    public void reloadPermissions() {
       reloadPermissions0();
    }

    protected void reloadPermissions0() {
        //Why do we need this? When the permissions manager reloads, it creates new instances to represent the same groups, so we need to reload our group instances.
        this.groups = new ArrayList<>();
        this.primaryGroup = null;
        CMongoGroupRepository groupRepository = playerRepository.groupRepository;
        if (groupRepository == null) throw new IllegalStateException("You need to setup the group repository for permissions to work!");
        if (groupIds != null) {
            for (ObjectId groupId : groupIds) {
                CGroup groupByObjectId = groupRepository.getGroupByObjectId(groupId);
                if (groupByObjectId == null) continue;
                this.groups.add(groupByObjectId);
            }
        }
        //Then we need to reload our permissions map.
        allPermissions = new HashMap<>(declaredPermissions);
        CGroup defaultGroup = groupRepository.getDefaultGroup();
        if (groups.size() == 0 && defaultGroup != null) processGroupInternal(defaultGroup);
        for (CGroup group : groups) {
            processGroupInternal(group);
        }

        //And now we get our primary group
        for (CGroup group : this.groups) {
            if (this.primaryGroup == null) {
                this.primaryGroup = group;
                continue;
            }
            if (this.primaryGroup.getPriority() < group.getPriority()) this.primaryGroup = group;
        }

        if (this.primaryGroup == null) this.primaryGroup = defaultGroup;
    }

    @Override
    public String getName() {
        return lastKnownUsername;
    }



    //Process a group into our allPermissions map, use care when calling as this can mess things up really bad.
    protected final synchronized void processGroupInternal(CGroup group) {
        Map<String, Boolean> groupPermissions = group.getAllPermissions();
        for (Map.Entry<String, Boolean> permission : groupPermissions.entrySet()) {
            String permNode = permission.getKey();
            if (!allPermissions.containsKey(permNode) || !allPermissions.get(permNode)) allPermissions.put(permNode, permission.getValue());
        }
    }

    @Override
    public boolean isDirectlyInGroup(CGroup group) {
        return groups.contains(group);
    }

    @Override
    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
    }

    @Override
    public void setDisplayName(String string) {
        if (string == null) {
            this.displayName = null;
            return;
        }
        this.displayName = ColorSupplements.translateAlternateColorCodes('&', string);
    }

    @Override
    public boolean hasDisplayName() {
        return displayName != null && !displayName.equals("");
    }

    @Override
    public void logIP(InetAddress address) {
        String hostAddress = address.getHostAddress();
        if (!knownIPAddresses.contains(hostAddress)) knownIPAddresses.add(hostAddress);
    }
}
