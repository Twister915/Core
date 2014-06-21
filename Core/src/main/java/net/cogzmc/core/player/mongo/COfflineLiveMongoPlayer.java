package net.cogzmc.core.player.mongo;

import com.mongodb.DBObject;
import lombok.NonNull;
import lombok.Synchronized;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.CPermissionsManager;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Joey on 6/21/2014.
 */
public class COfflineLiveMongoPlayer extends COfflineMongoPlayer implements GroupReloadObserver {
    protected COfflineLiveMongoPlayer(COfflineMongoPlayer otherCPlayer, CMongoPlayerRepository manager) {
        super(otherCPlayer, manager);
        Core.getPermissionsManager().registerObserver(this);
        reloadPermissions0();
    }

    public COfflineLiveMongoPlayer(UUID uniqueIdentifier, DBObject player, @NonNull CMongoPlayerRepository repository) {
        super(uniqueIdentifier, player, repository);
        Core.getPermissionsManager().registerObserver(this);
        reloadPermissions0();
    }

    @Override
    public void onReloadPermissions(CMongoPermissionsManager manager) {

    }

    //Reloads the allPermissions map based on declaredPermissions and inheritance
    @Synchronized
    private void reloadPermissions0() {
        //Why do we need this? When the permissions manager reloads, it creates new instances to represent the same groups, so we need to reload our group instances.
        this.groups = new ArrayList<>();
        this.primaryGroup = null;
        CPermissionsManager permissionsManager1 = Core.getPermissionsManager();
        assert permissionsManager1 instanceof CMongoPermissionsManager;
        CMongoPermissionsManager permissionsManager = (CMongoPermissionsManager) permissionsManager1;
        if (groupIds != null) {
            for (ObjectId groupId : groupIds) {
                CGroup groupByObjectId = permissionsManager.getGroupByObjectId(groupId);
                if (groupByObjectId == null) continue;
                this.groups.add(groupByObjectId);
            }
        }
        //Then we need to reload our permissions map.
        allPermissions = new HashMap<>(declaredPermissions);
        CGroup defaultGroup = permissionsManager.getDefaultGroup();
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
}
