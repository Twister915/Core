package net.cogzmc.core.player.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import lombok.*;
import net.cogzmc.core.player.CGroup;
import org.bson.types.ObjectId;

import java.util.*;

import static net.cogzmc.core.player.mongo.MongoUtils.combineObjectBuilders;
import static net.cogzmc.core.player.mongo.MongoUtils.getObjectForPermissible;

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
    @NonNull private String tablistColor;
    @NonNull private String chatColor;
    @NonNull private String chatPrefix;
    @NonNull private String chatSuffix;
    @Setter(AccessLevel.NONE) private Map<String, Boolean> allPermissions;
    private Integer priority = 0;

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
    public boolean isSet(String permission) {
        return this.declaredPermissions.containsKey(permission);
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
        builder.add(MongoKey.GROUPS_PRIORITY_KEY.toString(), priority);
        builder.add(MongoKey.GROUPS_CHAT_SUFFIX_KEY.toString(), chatSuffix);
        return builder.get();
    }

    @Override
    public void reloadPermissions() {
        allPermissions = new HashMap<>(declaredPermissions);

        Collections.sort(parents, new Comparator<CGroup>() {
            @Override
            public int compare(CGroup o1, CGroup o2) {
                return o1.getPriority()-o2.getPriority(); //Sort by priority.
            }
        }); //Sort the parents in order so that we end up going through the parents in the right order.
        //For every parent
        List<CGroup> parents1 = new ArrayList<>(parents);
        Collections.reverse(parents1);
        /*
         * Reverse the order of the parents so we end up putting the LOWEST priorities first, and overwriting them with
         * the highest priority permissions LAST.
         */

        for (CGroup parent : parents1) {
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
        if (group == this || group.getName().equals(getName())) throw new IllegalStateException("You cannot make this group parent itself!");
        checkForRecursiveParenthood(group);
        this.parents.add(group);
        reloadPermissions();
    }

    void checkForRecursiveParenthood(CGroup group) {
        if (group.isParent(this)) throw new IllegalStateException("You cannot set a child as a parent.");
        if (group.getParents().size() > 0)
            for (CGroup group2 : group.getParents())
                checkForRecursiveParenthood(group2);
    }

    @Override
    public void removeParent(CGroup group) {
        if (group == this || group.getName().equals(getName())) throw new IllegalStateException("You cannot make this group parent itself!");
        this.parents.remove(group);
        reloadPermissions();
    }

    @Override
    public boolean isParent(CGroup group) {
        if (parents.contains(group)) return true;
        for (CGroup parent : parents) {
            if (parent.isParent(group)) return true;
        }
        return false;
    }
}
