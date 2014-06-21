package net.cogzmc.core.player.mongo;

import net.cogzmc.core.player.*;

import java.lang.ref.WeakReference;
import java.util.*;

public final class CMongoPermissionsManager extends CMongoGroupRepository implements CPermissionsManager {
    //Hold the fields used as "class parameters" defining behavior.
    private final CPlayerManager playerManager;

    //Holds tracked values for the manager
    private final List<WeakReference<GroupReloadObserver>> groupReloadObservers = new LinkedList<>();

    public CMongoPermissionsManager(CMongoDatabase database, CMongoPlayerManager playerManager) {
        super(database, playerManager);
        this.playerManager = playerManager;
    }

    @Override
    public CGroup createNewGroup(String name) { //How to create a group:
        CGroup newGroup = super.createNewGroup(name);
        reloadPermissions(); //Reload our permissions
        return newGroup; //And return it
    }

    @Override
    public void deleteGroup(CGroup group) throws DatabaseConnectException {
        for (CPlayer player : getOnlineMembers(group)) {
            player.removeFromGroup(group);
        }
        super.deleteGroup(group);
    }

    private Set<CPlayer> getOnlineMembers(CGroup group) {
        Set<CPlayer> players = new HashSet<>();
        for (CPlayer player : playerManager) {
            if (player.isDirectlyInGroup(group)) players.add(player);
        }
        return players;
    }

    @Override
    public void reloadPermissions() {
        save();
        reloadGroups();
        Iterator<WeakReference<GroupReloadObserver>> iterator = groupReloadObservers.iterator();
        while (iterator.hasNext()) {
            GroupReloadObserver observer = iterator.next().get();
            if (observer == null) {
                iterator.remove();
                continue;
            }
            observer.onReloadPermissions(this);
        }
    }

    public void registerObserver(GroupReloadObserver observer) {
        this.groupReloadObservers.add(new WeakReference<>(observer));
    }

    @Override
    public void unregisterObserver(GroupReloadObserver observer) {
        Iterator<WeakReference<GroupReloadObserver>> iterator = groupReloadObservers.iterator();
        while (iterator.hasNext()) {
            WeakReference<GroupReloadObserver> next = iterator.next();
            GroupReloadObserver groupReloadObserver = next.get();
            if (groupReloadObserver == null || groupReloadObserver.equals(observer)) iterator.remove();
        }
    }
}
