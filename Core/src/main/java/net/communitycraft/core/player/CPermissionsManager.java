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
    /**
     * Creates a new {@link net.communitycraft.core.player.CGroup} with the name. The default variables for the group are put in place by the manager.
     * @param name The name of the group.
     * @return The newly created group (which should be saved into whatever database is being used)
     */
    CGroup createNewGroup(String name);

    /**
     * Gets a {@link net.communitycraft.core.player.CGroup} by name (basically a key for a group)
     * @param name The name of the group to find.
     * @return A group or {@code null} if there is no group to find.
     */
    CGroup getGroup(String name);

    /**
     * Gets the group that is marked as default. The first group to be created by {@link #createNewGroup(String)} will
     * be marked as default and be returned by this method.
     * @return The default {@link net.communitycraft.core.player.CGroup}
     */
    CGroup getDefaultGroup();

    /**
     * Sets the default group to something new. Also reloads permissions so that players will now observe this default
     * group and it's permissions.
     * @param group The {@link net.communitycraft.core.player.CGroup} to set as the default group.
     */
    void setDefaultGroup(CGroup group);

    /**
     * Deletes a group from the database and removes all members of the group from it.
     * @param group The {@link net.communitycraft.core.player.CGroup} to remove.
     * @throws DatabaseConnectException When we are unable to talk to the database and remove the group.
     */
    void deleteGroup(CGroup group) throws DatabaseConnectException;

    /**
     * Saves a group in the database.
     * @param group The {@link net.communitycraft.core.player.CGroup} to save into the database. This will update or create a group, but not reload permissions.
     */
    void saveGroup(CGroup group);

    /**
     * Gets all groups currently loaded into our permissions system
     * @return A {@link java.util.List} of {@link net.communitycraft.core.player.CGroup} objects representing the loaded groups.
     */
    List<CGroup> getGroups();

    /**
     * Reloads permissions and sends out a call to all registered {@link net.communitycraft.core.player.mongo.GroupReloadObserver}s.
     */
    void reloadPermissions();

    /**
     * Registers an observer for any call to {@link #reloadPermissions()}
     * @param observer The observer to register.
     */
    void registerObserver(GroupReloadObserver observer);

    /**
     * Un-registers an observer and also tests for any collected observers.
     * @param observer The observer to un-register.
     */
    void unregisterObserver(GroupReloadObserver observer);
}
