package net.communitycraft.core.player.mongo;

import com.mongodb.*;
import lombok.SneakyThrows;
import net.communitycraft.core.Core;
import net.communitycraft.core.player.*;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;

import java.util.*;

import static net.communitycraft.core.player.mongo.MongoUtils.getListFor;
import static net.communitycraft.core.player.mongo.MongoUtils.getPermissibileDataFor;
import static net.communitycraft.core.player.mongo.MongoUtils.getValueFrom;

final class CMongoPermissionsManager implements CPermissionsManager {
    private final CMongoDatabase database;
    private final CPlayerManager playerManager;
    private Map<String, CMongoGroup> groups;
    public CMongoPermissionsManager(CMongoDatabase database, CPlayerManager playerManager) {
        this.database = database;
        this.playerManager = playerManager;
        reloadPermissions();
    }

    @Override
    public CGroup createNewGroup(String name) {
        if (getGroup(name) != null) throw new IllegalStateException("Group already exists!");
        @SuppressWarnings("unchecked") CMongoGroup group = new CMongoGroup(name, Collections.EMPTY_MAP, Collections.EMPTY_LIST, ChatColor.WHITE, ChatColor.WHITE, name);
        saveGroup(group);
        return group;
    }

    @Override
    public CGroup getGroup(String name) {
        return groups.get(name);
    }

    CGroup getGroupByObjectId(ObjectId id) {
        for (CGroup cGroup : getGroups()) {
            if (cGroup.equals(id)) return cGroup;
        }
        return null;
    }

    @Override
    @SneakyThrows
    public void deleteGroup(CGroup group) {
        DBObject query = new BasicDBObjectBuilder().add(MongoKey.ID_KEY.toString(), ((CMongoGroup) group).getObjectId()).get();
        DBCollection groupsCollection = database.getCollection(MongoKey.GROUPS_COLLECTION.toString());
        DBObject andRemove = groupsCollection.findAndRemove(query);
        if (andRemove == null) throw new IllegalStateException("Group does not exist!");
        for (CPlayer cPlayer : group.getOnlineDirectMembers()) {
            cPlayer.removeFromGroup(group);
        }
        DBObject findPlayersInGroupQuery = new BasicDBObjectBuilder().add(MongoKey.USER_GROUPS_KEY.toString(), ((CMongoGroup) group).getObjectId()).get();
        DBCollection usersCollection = database.getCollection(MongoKey.USERS_COLLETION.toString());
        DBCursor playersInGroup = usersCollection.find(findPlayersInGroupQuery);
        CMongoPlayerManager mongoPlayerManager = (CMongoPlayerManager) playerManager;
        for (DBObject dbObject : playersInGroup) {
            COfflinePlayer playerInGroup = mongoPlayerManager.getOfflinePlayerByObjectId(getValueFrom(dbObject, MongoKey.ID_KEY, ObjectId.class));
            playerInGroup.removeFromGroup(group);
            playerInGroup.saveIntoDatabase();
        }
        this.groups.remove(group.getName());
        Core.logInfo("Removed group " + group.getName());
    }

    @Override
    public void saveGroup(CGroup group) {
        DBCollection collection = database.getCollection(MongoKey.GROUPS_COLLECTION.toString());
        CMongoGroup group1 = (CMongoGroup) group;
        DBObject dbObject = group1.getDBObject();
        collection.save(dbObject);
        group1.setObjectId(getValueFrom(dbObject, MongoKey.ID_KEY, ObjectId.class));
    }

    @Override
    public List<CGroup> getGroups() {
        return new ArrayList<CGroup>(groups.values());
    }

    @Override
    public void reloadPermissions() {
        this.groups = new HashMap<>();
        DBCollection groups = database.getCollection(MongoKey.GROUPS_COLLECTION.toString());
        for (DBObject dbObject : groups.find()) {
            CMongoGroup groupFor = getGroupFor(dbObject);
            if (groupFor == null) continue;
            this.groups.put(groupFor.getName(), groupFor);
        }
        for (CGroup cGroup : getGroups()) {
            cGroup.reloadPermissions();
        }
    }

    CMongoGroup getGroupFor(DBObject object) {
        String name = getValueFrom(object, MongoKey.GROUPS_NAME_KEY, String.class);
        List<ObjectId> parentIds = getListFor(getValueFrom(object, MongoKey.GROUPS_PARENTS_KEY, BasicDBList.class), ObjectId.class);
        List<CGroup> parents = new ArrayList<>();
        for (ObjectId parentId : parentIds) {
            parents.add(getGroupByObjectId(parentId));
        }
        ObjectId objectId = getValueFrom(object, MongoKey.ID_KEY, ObjectId.class);
        CPermissible perm = getPermissibileDataFor(object);
        CMongoGroup cMongoGroup = new CMongoGroup(name, perm.getDeclaredPermissions(), parents, perm.getTablistColor(), perm.getChatColor(), perm.getChatPrefix());
        cMongoGroup.setObjectId(objectId);
        return cMongoGroup;
    }
}
