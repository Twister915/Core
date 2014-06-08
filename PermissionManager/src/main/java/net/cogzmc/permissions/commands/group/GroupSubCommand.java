package net.cogzmc.permissions.commands.group;

import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.permissions.commands.permissibile.*;

public final class GroupSubCommand extends ModuleCommand {
    public static final GroupResolutionDelegate GROUP_RESOLUTION_DELEGATE = new GroupResolutionDelegate();
    public GroupSubCommand() {
        super("group",
                new HasSubCommand<>(GROUP_RESOLUTION_DELEGATE),
                new SetSubCommand<>(GROUP_RESOLUTION_DELEGATE),
                new UnsetSubCommand<>(GROUP_RESOLUTION_DELEGATE),
                new ChatColorSubCommand<>(GROUP_RESOLUTION_DELEGATE),
                new PrefixSubCommand<>(GROUP_RESOLUTION_DELEGATE),
                new CreateSubCommand(),
                new DeleteSubCommand(),
                new ListSubCommand());
    }

    @Override
    public boolean isUsingSubCommandsOnly() {
        return true;
    }
}
