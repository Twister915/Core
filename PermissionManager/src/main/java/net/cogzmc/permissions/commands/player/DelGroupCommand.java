package net.cogzmc.permissions.commands.player;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CGroup;
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
        CGroup group = Core.getPermissionsManager().getGroup(arg);
        assert group != null;
        if (!offlinePlayer.isDirectlyInGroup(group)) throw new CommandException("The player is already not in that group!");
        offlinePlayer.removeFromGroup(group);
    }
}
