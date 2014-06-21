package net.cogzmc.core.player;

import java.util.Map;

/**
 * Interface for anything that can have permissions and their associated metadata in the Core permission system.
 */
public interface CPermissible {
    /**
     *
     */
    String getChatColor();

    /**
     *
     * @return
     */
    String getTablistColor();

    /**
     *
     * @return
     */
    String getChatPrefix();

    /**
     *
     * @return
     */
    String getChatSuffix();

    /**
     *
     * @param color
     */
    void setChatColor(String color);

    /**
     *
     * @param color
     */
    void setTablistColor(String color);

    /**
     *
     * @param prefix
     */
    void setChatPrefix(String prefix);

    /**
     *
     * @param suffix
     */
    void setChatSuffix(String suffix);

    /**
     *
     * @param permission
     * @param value
     */
    void setPermission(String permission, Boolean value);

    /**
     *
     * @param permission
     */
    void unsetPermission(String permission);

    /**
     *
     * @param permission
     * @return
     */
    boolean hasPermission(String permission);

    /**
     *
     * @param permission
     * @return
     */
    boolean isSet(String permission);

    /**
     *
     * @return
     */
    Map<String, Boolean> getDeclaredPermissions();

    /**
     *
     */
    void reloadPermissions();

    /**
     *
     * @return
     */
    String getName();
}
