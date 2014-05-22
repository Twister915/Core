package net.communitycraft.permissions.commands.group;

import net.communitycraft.core.modular.command.ModuleCommand;
import net.communitycraft.permissions.commands.permissibile.GroupResolutionDelegate;
import net.communitycraft.permissions.commands.permissibile.HasSubCommand;

public final class GroupSubCommand extends ModuleCommand {
    private static final GroupResolutionDelegate groupDelegate = new GroupResolutionDelegate();
    public GroupSubCommand() {
        super("group", new HasSubCommand<>(groupDelegate));
    }
}
