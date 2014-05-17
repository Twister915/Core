package me.twister915.core.player;

import org.bukkit.ChatColor;

import java.util.Map;

/**
 *
 */
public interface CPermissible {
    /**
     *
     * @return
     */
    ChatColor getChatColor();

    /**
     *
     * @return
     */
    ChatColor getTablistColor();

    /**
     *
     * @return
     */
    String getChatPrefix();

    /**
     *
     * @param color
     */
    void setChatColor(ChatColor color);

    /**
     *
     * @param color
     */
    void setTablistColor(ChatColor color);

    /**
     *
     * @param prefix
     */
    void setChatPrefix(String prefix);

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
     * @return
     */
    Map<String, Boolean> getDeclaredPermissions();

    /**
     *
     */
    void reloadPermissions();
}
