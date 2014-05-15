package net.communitycraft.core.player;

import org.bukkit.ChatColor;

public interface CPermissable {
    ChatColor getChatColor();
    ChatColor getTablistColor();
    String getChatPrefix();
    void setChatColor(ChatColor color);
    void setTablistColor(ChatColor color);
    void setChatPrefix(String prefix);
    void setPermission(String permission, Boolean value);
    boolean hasPermission(String permission);
}
