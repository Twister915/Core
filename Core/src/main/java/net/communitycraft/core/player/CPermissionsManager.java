package net.communitycraft.core.player;

import net.communitycraft.core.player.mongo.GroupReloadObserver;

import java.util.List;

/**
 * The {@link net.communitycraft.core.player.CPermissionsManager} is responsible for managing groups.
 *
 * When you create a group, if no others exist that group will be listed as the <b>default</b> group. Being marked as the
 * default group will be added to a player always.
 *
 * The {@link #reloadPermissions()} method should be called to reload groups from the
 */
public interface CPermissionsManager {
    CGroup createNewGroup(String name);
    CGroup getGroup(String name);
    CGroup getDefaultGroup();
    void setDefaultGroup(CGroup group);
    void deleteGroup(CGroup group) throws DatabaseConnectException;
    void saveGroup(CGroup group);
    List<CGroup> getGroups();
    void reloadPermissions();
    void registerObserver(GroupReloadObserver observer);
}
