package net.communitycraft.permissions.commands.general;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.CGroup;

public abstract class AbstractGroupSubCommand extends PermissibleSubCommand<CGroup> {
    protected AbstractGroupSubCommand(String name) {
        super(name);
    }

    @Override
    protected CGroup getPermissible(String name) {
        return Core.getPermissionsManager().getGroup(name);
    }

    @Override
    protected String getNameOfPermissibleType() {
        return "Group";
    }
}
