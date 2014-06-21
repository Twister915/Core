package net.cogzmc.core.player;

import net.cogzmc.core.player.mongo.GroupReloadObserver;

import java.util.List;

/**
 * The {@link net.cogzmc.core.player.CPermissionsManager} is responsible for managing groups.
 *
 * When you create a group, if no others exist that group will be listed as the <b>default</b> group. Being marked as the
 * default group will be added to a player always.
 *
 * The {@link #reloadPermissions()} method should be called to reload groups from the database.
 *
 * @since 1.0
 * @author Joey
 * @see net.cogzmc.core.player.CPermissible
 * @see net.cogzmc.core.player.CGroup
 */
public interface CPermissionsManager extends CGroupRepository {
    /**
     * Reloads permissions and sends out a call to all registered {@link net.cogzmc.core.player.mongo.GroupReloadObserver}s.
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

    /**
     * Saves the current values into the database.
     */
    void save();
}
