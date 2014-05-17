package me.twister915.core.player.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import lombok.*;
import me.twister915.core.Core;
import me.twister915.core.player.CGroup;
import me.twister915.core.player.CPlayer;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.twister915.core.player.mongo.MongoUtils.combineObjectBuilders;
import static me.twister915.core.player.mongo.MongoUtils.getObjectForPermissible;

@Data
@EqualsAndHashCode(of = {"name", "objectId", "parents"})
final class CMongoGroup implements CGroup {
    /* Group stuff */
    @NonNull private final String name;
    @NonNull @Getter(AccessLevel.NONE) private final Map<String, Boolean> declaredPermissions;
    @NonNull private final List<CGroup> parents;
    /* Mongo stuff */
    private ObjectId objectId;

    /* Group stuff */
    @NonNull private ChatColor tablistColor;
    @NonNull private ChatColor chatColor;
    @NonNull private String chatPrefix;
    @Setter(AccessLevel.NONE) private Map<String, Boolean> allPermissions;

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
        return this.allPermissions.containsKey(permission) && this.allPermissions.get(permission);
    }

    @Override
    public Map<String, Boolean> getDeclaredPermissions() {
        return new HashMap<>(declaredPermissions);
    }

    DBObject getDBObject() {
        BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
        builder.add(MongoKey.GROUPS_NAME_KEY.toString(), name);
        if (objectId != null) builder.add(MongoKey.ID_KEY.toString(), objectId);
        combineObjectBuilders(builder, getObjectForPermissible(this)); //Does all the permissible stuff.
        BasicDBList parentList = new BasicDBList();
        for (CGroup parent : parents) {
            CMongoGroup parent1 = (CMongoGroup) parent;
            parentList.add(parent1.getObjectId());
        }
        builder.add(MongoKey.GROUPS_PARENTS_KEY.toString(), parentList);
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
    public void reloadPermissions() {
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

    @Override
    public void addParent(CGroup group) {
        checkForRecursiveParenthood(group);
        this.parents.add(group);
        reloadPermissions();
    }

    void checkForRecursiveParenthood(CGroup group) {
        if (isParent(group)) throw new IllegalStateException("You cannot parent a group that is your parent.");
        if (group.getParents().size() > 0)
            for (CGroup group2 : group.getParents())
                checkForRecursiveParenthood(group2);
    }

    @Override
    public void removeParent(CGroup group) {
        this.parents.remove(group);
        reloadPermissions();
    }

    @Override
    public boolean isParent(CGroup group) {
        return parents.contains(group);
    }
}
