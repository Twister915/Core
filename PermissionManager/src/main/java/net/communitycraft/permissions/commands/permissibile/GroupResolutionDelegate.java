package net.communitycraft.permissions.commands.permissibile;

import net.communitycraft.core.Core;
import net.communitycraft.core.player.CGroup;

public final class GroupResolutionDelegate implements PermissibleResolutionDelegate<CGroup> {
    @Override
    public CGroup getFor(String name) {
        return Core.getPermissionsManager().getGroup(name);
    }
}
