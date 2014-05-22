package net.communitycraft.permissions.commands.player;

import net.communitycraft.core.modular.command.ModuleCommand;
import net.communitycraft.permissions.commands.permissibile.HasSubCommand;
import net.communitycraft.permissions.commands.permissibile.PlayerResolutionDelegate;

public final class PlayerSubCommand extends ModuleCommand {
    private static final PlayerResolutionDelegate playerResolutionDelegate = new PlayerResolutionDelegate();
    public PlayerSubCommand() {
        super("player",
                new SetGroupCommand(),
                new AddGroupCommand(),
                new DelGroupCommand(),
                new HasSubCommand<>(playerResolutionDelegate)
        );
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
