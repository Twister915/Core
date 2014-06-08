package net.cogzmc.permissions.commands.player;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.permissions.commands.general.AbstractPlayerSubCommand;

import java.util.List;

public final class DelGroupCommand extends AbstractPlayerSubCommand {
    protected DelGroupCommand() {
        super("delgroup");
    }

    @Override
    protected boolean needsSecondArgument() {
        return true;
    }

    @Override
    protected List<String> getComplete(String arg) {
        return null;
    }

    @Override
    protected void doAction(COfflinePlayer offlinePlayer, String arg) throws CommandException {

    }
}
