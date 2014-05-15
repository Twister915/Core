package net.communitycraft.core.player.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import lombok.*;
import net.communitycraft.core.Core;
import net.communitycraft.core.player.CGroup;
import net.communitycraft.core.player.CPermissible;
import net.communitycraft.core.player.CPlayer;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.communitycraft.core.player.mongo.COfflineMongoPlayer.*;

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
        combineObjectBuilders(builder, getObjectForPermissible(this)); //Does all the permissible stuff.
        BasicDBList parentList = new BasicDBList();
        for (CGroup parent : parents) {
            CMongoGroup parent1 = (CMongoGroup) parent;
            parentList.add(parent1.getObjectId());
        }
        builder.add(MongoKey.GROUPS_PARENTS_KEY.toString(), parentList);
        return builder.get();
    }

    static BasicDBObjectBuilder getObjectForPermissible(CPermissible permissible) {
        BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
        builder.add(MongoKey.GROUPS_TABLIST_COLOR_KEY.toString(), permissible.getTablistColor().name());
        builder.add(MongoKey.GROUPS_CHAT_COLOR_KEY.toString(), permissible.getChatColor().name());
        builder.add(MongoKey.GROUPS_CHAT_PREFIX_KEY.toString(), permissible.getChatPrefix());
        builder.add(MongoKey.GROUPS_PERMISSIONS_KEY.toString(), getDBObjectFor(permissible.getDeclaredPermissions()));
        return builder;
    }

    static void combineObjectBuilders(BasicDBObjectBuilder h, BasicDBObjectBuilder k) {
        DBObject kObject = k.get();
        for (String s : kObject.keySet()) {
            h.add(s, kObject.get(s));
        }
    }

    static CPermissible getPermissibileDataFor(DBObject object) {
        final Map<String, Boolean> declaredPermissions = getMapFor(getValueFrom(object, MongoKey.GROUPS_PERMISSIONS_KEY, BasicDBObject.class), Boolean.class);
        final ChatColor chatColor = ChatColor.valueOf(getValueFrom(object, MongoKey.GROUPS_CHAT_COLOR_KEY, String.class));
        final ChatColor tablistColor = ChatColor.valueOf(getValueFrom(object, MongoKey.GROUPS_TABLIST_COLOR_KEY, String.class));
        final String chatPrefix = getValueFrom(object, MongoKey.GROUPS_CHAT_PREFIX_KEY, String.class);
        return new CPermissible() {
            @Override
            public ChatColor getChatColor() {
                return chatColor;
            }

            @Override
            public ChatColor getTablistColor() {
                return tablistColor;
            }

            @Override
            public String getChatPrefix() {
                return chatPrefix;
            }

            @Override
            public void setChatColor(ChatColor color) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public void setTablistColor(ChatColor color) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public void setChatPrefix(String prefix) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public void setPermission(String permission, Boolean value) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public boolean hasPermission(String permission) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public Map<String, Boolean> getDeclaredPermissions() {
                return declaredPermissions;
            }

            @Override
            public void reloadPermissions() {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }
        };
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
        reloadPermissionsFromInheritence();
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
