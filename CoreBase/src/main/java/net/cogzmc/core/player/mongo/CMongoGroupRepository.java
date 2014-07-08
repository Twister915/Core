package net.cogzmc.core.player.mongo;

import com.mongodb.*;
import lombok.Getter;
import net.cogzmc.core.player.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.cogzmc.core.player.mongo.MongoUtils.getListFor;
import static net.cogzmc.core.player.mongo.MongoUtils.getPermissibileDataFor;
import static net.cogzmc.core.player.mongo.MongoUtils.getValueFrom;

public class CMongoGroupRepository implements CGroupRepository {
    private final CMongoDatabase database;
    private final static String DEFAULT_COLOR = "\u00A7f";

    @Getter private CGroup defaultGroup;
    private final CMongoPlayerRepository playerRepository;
    private Map<String, CMongoGroup> groups;

    public CMongoGroupRepository(CMongoDatabase database, CMongoPlayerRepository playerRepository) {
        this.database = database;
        this.playerRepository = playerRepository;
        reloadGroups();
    }

    @Override
    public CGroup createNewGroup(String name) {
        if (getGroup(name) != null) throw new IllegalStateException("Group already exists!"); //Check if we already have this group name
        CMongoGroup group =
                new CMongoGroup(name, new HashMap<String, Boolean>(), new ArrayList<CGroup>(), DEFAULT_COLOR, DEFAULT_COLOR, name, ""); //Setup some default values
        saveGroup(group); //Save the group
        if (this.getDefaultGroup() == null) setDefaultGroup(group); //Set this as the default group
        return group;
    }

    @Override
    public CGroup getGroup(String name) {
        return groups.get(name.toLowerCase());
    }

    @Override
    public void setDefaultGroup(CGroup group) {
        CGroup defaultGroup1 = this.defaultGroup;
        this.defaultGroup = null;
        if (defaultGroup1 != null) saveGroup(defaultGroup1);
        this.defaultGroup = group;
        saveGroup(group);
    }

    @Override
    public void deleteGroup(CGroup group) throws DatabaseConnectException {
        DBObject query = new BasicDBObjectBuilder().add(MongoKey.ID_KEY.toString(), ((CMongoGroup) group).getObjectId()).get();
        DBCollection groupsCollection = database.getCollection(MongoKey.GROUPS_COLLECTION.toString());
        DBObject andRemove = groupsCollection.findAndRemove(query);
        if (andRemove == null) throw new IllegalStateException("Group does not exist!");
        DBObject findPlayersInGroupQuery = new BasicDBObjectBuilder().add(MongoKey.USER_GROUPS_KEY.toString(), ((CMongoGroup) group).getObjectId()).get();
        DBCollection usersCollection = database.getCollection(MongoKey.USERS_COLLETION.toString());
        DBCursor playersInGroup = usersCollection.find(findPlayersInGroupQuery);
        for (DBObject dbObject : playersInGroup) {
            COfflinePlayer playerInGroup = playerRepository.getOfflinePlayerByObjectId(getValueFrom(dbObject, MongoKey.ID_KEY, ObjectId.class));
            playerInGroup.removeFromGroup(group);
            playerInGroup.saveIntoDatabase();
        }
        this.groups.remove(group.getName());
    }

    @Override
    public void saveGroup(CGroup group) {
        DBCollection collection = database.getCollection(MongoKey.GROUPS_COLLECTION.toString());
        CMongoGroup group1 = (CMongoGroup) group;
        DBObject dbObject = group1.getDBObject();
        if (defaultGroup != null && defaultGroup.equals(group1)) dbObject.put(MongoKey.GROUPS_DEFAULT_MARKER.toString(), true);
        else if (dbObject.containsField(MongoKey.GROUPS_DEFAULT_MARKER.toString())) dbObject.removeField(MongoKey.GROUPS_DEFAULT_MARKER.toString());
        collection.save(dbObject);
        group1.setObjectId(getValueFrom(dbObject, MongoKey.ID_KEY, ObjectId.class));
    }

    @Override
    public void reloadGroups() {
        this.groups = new HashMap<>();
        this.defaultGroup = null;
        DBCollection groups = database.getCollection(MongoKey.GROUPS_COLLECTION.toString());
        for (DBObject dbObject : groups.find()) {
            CMongoGroup groupFor = getGroupFor(dbObject);
            if (groupFor == null) continue;
            this.groups.put(groupFor.getName().toLowerCase(), groupFor);
            if (dbObject.containsField(MongoKey.GROUPS_DEFAULT_MARKER.toString())) this.defaultGroup = groupFor;
        }
        for (CGroup cGroup : getGroups()) {
            cGroup.reloadPermissions();
        }
    }

    @Override
    public void save() {
        if (groups == null) return;
        for (CMongoGroup cMongoGroup : groups.values()) {
            saveGroup(cMongoGroup);
        }
    }

    @Override
    public List<CGroup> getGroups() {
        return new ArrayList<CGroup>(groups.values());
    }

    CMongoGroup getGroupFor(DBObject object) {
        String name = getValueFrom(object, MongoKey.GROUPS_NAME_KEY, String.class);
        List<ObjectId> parentIds = getListFor(getValueFrom(object, MongoKey.GROUPS_PARENTS_KEY, BasicDBList.class), ObjectId.class);
        List<CGroup> parents = new ArrayList<>();
        for (ObjectId parentId : parentIds) {
            parents.add(getGroupByObjectId(parentId));
        }
        ObjectId objectId = getValueFrom(object, MongoKey.ID_KEY, ObjectId.class);
        Integer priority = getValueFrom(object, MongoKey.GROUPS_PRIORITY_KEY, Integer.class);
        CPermissible perm = getPermissibileDataFor(object);
        CMongoGroup cMongoGroup = new CMongoGroup(name, perm.getDeclaredPermissions(), parents, perm.getTablistColor(), perm.getChatColor(), perm.getChatPrefix(), perm.getChatSuffix());
        cMongoGroup.setObjectId(objectId);
        cMongoGroup.setPriority(priority == null ? 0 : priority);
        return cMongoGroup;
    }

    CGroup getGroupByObjectId(ObjectId id) {
        for (CGroup cGroup : getGroups()) {
            if (((CMongoGroup)cGroup).getObjectId().equals(id)) return cGroup;
        }
        return null;
    }
}
