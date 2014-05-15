package net.communitycraft.core.player.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.communitycraft.core.player.CGroup;
import net.communitycraft.core.player.CPermissionsManager;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.communitycraft.core.player.mongo.COfflineMongoPlayer.*;

class CMongoPermissionsManager implements CPermissionsManager {
    private final CMongoDatabase database;
    private Map<String, CMongoGroup> groups;
    public CMongoPermissionsManager(CMongoDatabase database) {
        this.database = database;
        groups = new HashMap<>();
    }

    @Override
    public CGroup createNewGroup(String name) {
        return null;
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
    public void deleteGroup(CGroup group) {

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
        Map<String, Boolean> declaredPermissions = getMapFor(getValueFrom(object, MongoKey.GROUPS_PERMISSIONS_KEY, BasicDBObject.class), Boolean.class);
        ChatColor chatColor = ChatColor.valueOf(getValueFrom(object, MongoKey.GROUPS_CHAT_COLOR_KEY, String.class));
        ChatColor tablistColor = ChatColor.valueOf(getValueFrom(object, MongoKey.GROUPS_TABLIST_COLOR_KEY, String.class));
        String chatPrefix = getValueFrom(object, MongoKey.GROUPS_CHAT_PREFIX_KEY, String.class);
        ObjectId objectId = getValueFrom(object, MongoKey.ID_KEY, ObjectId.class);
        return new CMongoGroup(name, declaredPermissions, parents, objectId, tablistColor, chatColor, chatPrefix);
    }
}
