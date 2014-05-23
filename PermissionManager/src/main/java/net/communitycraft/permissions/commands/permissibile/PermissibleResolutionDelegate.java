package net.communitycraft.permissions.commands.permissibile;

import net.cogzmc.core.player.CPermissible;

public interface PermissibleResolutionDelegate<T extends CPermissible> {
    T getFor(String name);
    String getNameOfType();
}
