package net.cogzmc.core.player;

import java.util.List;

public interface CGroupRepository {
    /**
     * Creates a new {@link net.cogzmc.core.player.CGroup} with the name. The default variables for the group are put in place by the manager.
     * @param name The name of the group.
     * @return The newly created group (which should be saved into whatever database is being used)
     */
    CGroup createNewGroup(String name);

    /**
     * Gets a {@link net.cogzmc.core.player.CGroup} by name (basically a key for a group)
     * @param name The name of the group to find.
     * @return A group or {@code null} if there is no group to find.
     */
    CGroup getGroup(String name);
    /**
     * Gets the group that is marked as default. The first group to be created by {@link #createNewGroup(String)} will
     * be marked as default and be returned by this method.
     * @return The default {@link net.cogzmc.core.player.CGroup}
     */
    CGroup getDefaultGroup();

    /**
     * Sets the default group to something new. Also reloads permissions so that players will now observe this default
     * group and it's permissions.
     * @param group The {@link net.cogzmc.core.player.CGroup} to set as the default group.
     */
    void setDefaultGroup(CGroup group);

    /**
     * Deletes a group from the database and removes all members of the group from it.
     * @param group The {@link net.cogzmc.core.player.CGroup} to remove.
     * @throws DatabaseConnectException When we are unable to talk to the database and remove the group.
     */
    void deleteGroup(CGroup group) throws DatabaseConnectException;

    /**
     * Saves a group in the database.
     * @param group The {@link net.cogzmc.core.player.CGroup} to save into the database. This will update or create a group, but not reload permissions.
     */
    void saveGroup(CGroup group);

    /**
     * Gets all groups currently loaded into our permissions system
     * @return A {@link java.util.List} of {@link net.cogzmc.core.player.CGroup} objects representing the loaded groups.
     */
    List<CGroup> getGroups();

    boolean isDefaultGroup(CGroup group);

    void reloadGroups();

    void save();
}
