package net.communitycraft.permissions.commands.player;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.COfflinePlayer;
import net.communitycraft.permissions.commands.general.AbstractPlayerSubCommand;

public final class DelGroupCommand extends AbstractPlayerSubCommand {
    protected DelGroupCommand() {
        super("delgroup");
    }

    @Override
    protected boolean needsSecondArgument() {
        return true;
    }

    @Override
    protected void doAction(COfflinePlayer offlinePlayer, String arg) throws CommandException {

    }
}
