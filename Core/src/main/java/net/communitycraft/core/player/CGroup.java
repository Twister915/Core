package net.communitycraft.core.player;

import org.bukkit.ChatColor;

import java.util.List;

public interface CGroup extends CPermissable {
    String getName();
    List<CGroup> getParents();
    List<String> getDeclaredPermissions();
    List<String> getAllPermissions();
}
