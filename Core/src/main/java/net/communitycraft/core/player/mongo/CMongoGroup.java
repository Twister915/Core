package net.communitycraft.core.player.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import lombok.*;
import net.communitycraft.core.Core;
import net.communitycraft.core.player.CGroup;
import net.communitycraft.core.player.CPlayer;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.communitycraft.core.player.mongo.COfflineMongoPlayer.getDBObjectFor;

@Data
class CMongoGroup implements CGroup {
    /* Group stuff */
    @NonNull private final String name;
    @NonNull @Getter(AccessLevel.NONE) private final Map<String, Boolean> declaredPermissions;
    @NonNull private final List<CGroup> parents;
    /* Mongo stuff */
    private final ObjectId objectId;

    /* Group stuff */
    @NonNull private ChatColor tablistColor;
    @NonNull private ChatColor chatColor;
    @NonNull private String chatPrefix;
    @Setter(AccessLevel.NONE) private Map<String, Boolean> allPermissions;

    @Override
    public void setPermission(String permission, Boolean value) {
        this.declaredPermissions.put(permission, value);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.allPermissions.containsKey(permission) && this.allPermissions.get(permission);
    }

    @Override
    public Map<String, Boolean> getDeclaredPermissions() {
        return new HashMap<>(declaredPermissions);
    }

    void reloadPermissionsFromInheritence() {
        allPermissions = new HashMap<>(declaredPermissions);
        //For every parent
        for (CGroup parent : parents) {
            //get their permissions (inherited on their tree well)
            Map<String, Boolean> allPermissions1 = parent.getAllPermissions();
            //Iterate through them
            for (Map.Entry<String, Boolean> stringBooleanEntry : allPermissions1.entrySet()) {
                //...and if we don't have it, or ours is false
                if (!allPermissions.containsKey(stringBooleanEntry.getKey()) || !allPermissions.get(stringBooleanEntry.getKey())) {
                    allPermissions.put(stringBooleanEntry.getKey(), stringBooleanEntry.getValue()); //Put it into ours
                }
            }
        }
    }

    DBObject getDBObject() {
        BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
        builder.add(MongoKey.GROUPS_NAME_KEY.toString(), name);
        if (objectId != null) builder.add(MongoKey.ID_KEY.toString(), objectId);
        builder.add(MongoKey.GROUPS_TABLIST_COLOR_KEY.toString(), tablistColor.name());
        builder.add(MongoKey.GROUPS_CHAT_COLOR_KEY.toString(), chatColor.name());
        builder.add(MongoKey.GROUPS_CHAT_PREFIX_KEY.toString(), chatPrefix);
        BasicDBList parentList = new BasicDBList();
        for (CGroup parent : parents) {
            parentList.add(((CMongoGroup)parent).getObjectId());
        }
        builder.add(MongoKey.GROUPS_PARENTS_KEY.toString(), parentList);
        builder.add(MongoKey.GROUPS_PERMISSIONS_KEY.toString(), getDBObjectFor(declaredPermissions));
        return builder.get();
    }

    @Override
    public List<CPlayer> getOnlineDirectMembers() {
        List<CPlayer> players = new ArrayList<>();
        for (CPlayer cPlayer : Core.getPlayerManager()) {
            if (cPlayer.getGroups().contains(this)) players.add(cPlayer);
        }
        return players;
    }

    @Override
    public void reload() {
        reloadPermissionsFromInheritence();
    }
}
