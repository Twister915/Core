package net.communitycraft.permissions.commands.permissibile;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.CGroup;

public final class GroupResolutionDelegate implements PermissibleResolutionDelegate<CGroup> {
    @Override
    public CGroup getFor(String name) {
        return Core.getPermissionsManager().getGroup(name);
    }
}
