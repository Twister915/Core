package net.cogzmc.permissions.commands.permissibile;

import net.cogzmc.core.player.CPermissible;

import java.util.List;

interface PermissibleResolutionDelegate<T extends CPermissible> {
    T getFor(String name);
    String getNameOfType();
    List<String> getAutoCompleteFor(String s);
}
