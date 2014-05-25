package net.communitycraft.permissions.commands.group;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CGroup;
import net.communitycraft.permissions.PermissionsManager;
import org.bukkit.command.CommandSender;

public final class ListSubCommand extends ModuleCommand {
    protected ListSubCommand() {
        super("list");
    }

    @Override
    public void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        StringBuilder builder = new StringBuilder();
        for (CGroup cGroup : Core.getPermissionsManager().getGroups()) {
            builder.append(cGroup.getName()).append(", ");
        }
        String groupList = builder.toString();
        groupList = groupList.substring(0, groupList.length()-2);
        sender.sendMessage(PermissionsManager.getInstance().getFormat("list-groups", new String[]{"<groups>", groupList}));
    }
}
