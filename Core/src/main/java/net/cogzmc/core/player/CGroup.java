package net.cogzmc.core.player;

import java.util.List;
import java.util.Map;

/**
 * This represents a group that a player can be a member of.
 *
 * @author Joey
 * @since 1.0
 */
public interface CGroup extends CPermissible {
    /**
     * Gets the groups that this group inherits from.
     * @return The {@link net.cogzmc.core.player.CGroup}'s parents in a {@link java.util.List} of {@link net.cogzmc.core.player.CGroup} instances.
     */
    List<CGroup> getParents();

    /**
     * Adds a group as a parent of this group. Will throw an exception if this causes a looped parenthood.
     *
     * If this group is a parent of the group you're attempting to establish as a child, this causes a looped parenthood and will
     * throw an {@link java.lang.IllegalStateException}.
     *
     * @param group The {@link net.cogzmc.core.player.CGroup} to make a parent.
     */
    void addParent(CGroup group);

    /**
     * The group to remove parenthood from. If the group is not the parent, no action will be performed, if the group is
     * this group we will throw an {@link java.lang.IllegalStateException}.
     * @param group Removes a {@link net.cogzmc.core.player.CGroup} as parent.
     */
    void removeParent(CGroup group);

    /**
     * Checks if a {@link net.cogzmc.core.player.CGroup} holds a "parent -> child" relationship to this {@link net.cogzmc.core.player.CGroup}.
     * @param group The {@link net.cogzmc.core.player.CGroup} to check parenthood of.
     * @return A {@code boolean} representing parenthood.
     */
    boolean isParent(CGroup group);

    /**
     * Gets all the permissions that a group has, including it's inherited permissions with priorities.
     * @return
     */
    Map<String, Boolean> getAllPermissions();

    /**
     *
     * @return
     */
    List<CPlayer> getOnlineDirectMembers();

    /**
     *
     * @return
     */
    Integer getPriority();

    /**
     *
     * @param priority
     */
    void setPriority(Integer priority);
}
