package net.communitycraft.core.player;

import org.bukkit.ChatColor;

import java.util.Map;

public interface CPermissible {
    ChatColor getChatColor();
    ChatColor getTablistColor();
    String getChatPrefix();
    void setChatColor(ChatColor color);
    void setTablistColor(ChatColor color);
    void setChatPrefix(String prefix);
    void setPermission(String permission, Boolean value);
    void unsetPermission(String permission);
    boolean hasPermission(String permission);
    Map<String, Boolean> getDeclaredPermissions();
    void reloadPermissions();
}
