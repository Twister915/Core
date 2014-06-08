package net.cogzmc.permissions.commands.player;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.permissions.commands.general.AbstractPlayerSubCommand;
import net.cogzmc.permissions.commands.group.GroupSubCommand;

import java.util.List;

public final class AddGroupCommand extends AbstractPlayerSubCommand {
    protected AddGroupCommand() {
        super("addgroup");
    }

    @Override
    protected boolean needsSecondArgument() {
        return true;
    }

    @Override
    protected List<String> getComplete(String arg) {
        return GroupSubCommand.GROUP_RESOLUTION_DELEGATE.getAutoCompleteFor(arg);
    }

    @Override
    protected void doAction(COfflinePlayer offlinePlayer, String arg) throws CommandException {
        CGroup group = Core.getPermissionsManager().getGroup(arg);
        assert group != null;
        if (offlinePlayer.isDirectlyInGroup(group)) throw new CommandException("The player is already in this group!");
        offlinePlayer.addToGroup(group);
    }

    @Override
    protected boolean validateArgument(String arg) {
        return Core.getPermissionsManager().getGroup(arg) != null;
    }
}
