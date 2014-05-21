package net.communitycraft.permissions.commands.player;

import net.communitycraft.core.modular.command.ModuleCommand;

public final class PlayerSubCommand extends ModuleCommand {
    public PlayerSubCommand() {
        super("player",
                new SetGroupCommand(),
                new AddGroupCommand(),
                new DelGroupCommand()
        );
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
