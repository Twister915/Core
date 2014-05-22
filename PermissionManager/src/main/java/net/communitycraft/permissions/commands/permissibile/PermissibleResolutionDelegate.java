package net.communitycraft.permissions.commands.permissibile;

import net.communitycraft.core.player.CPermissible;

public interface PermissibleResolutionDelegate<T extends CPermissible> {
    T getFor(String name);
    String getNameOfType();
}
