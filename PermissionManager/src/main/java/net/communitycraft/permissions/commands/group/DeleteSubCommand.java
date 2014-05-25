package net.communitycraft.permissions.commands.group;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPermissionsManager;
import net.cogzmc.core.player.DatabaseConnectException;
import net.communitycraft.permissions.PermissionsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class DeleteSubCommand extends ModuleCommand {
    public DeleteSubCommand() {
        super("delete");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) throw new ArgumentRequirementException("You must supply a group name!"); //Test the args
        String targetGroupName = args[0]; //Get the group name
        CPermissionsManager permissionsManager = Core.getPermissionsManager(); //LV because method calls = expensive
        if (permissionsManager.getGroup(targetGroupName) == null) throw new ArgumentRequirementException("The group you specified does not exist!"); //Check if group exists
        try { //Try to
            permissionsManager.deleteGroup(permissionsManager.getGroup(targetGroupName)); //Delete the group
        } catch (DatabaseConnectException e) { //and if we fail
            e.printStackTrace(); //Print the stack trace
            throw new CommandException("Unable to delete group for unknown reason!"); //and let the user know.
        } //Otherwise
        sender.sendMessage(PermissionsManager.getInstance().getFormat("delete-group", new String[]{"<group>", targetGroupName})); //Let the user know we deleted the group.
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return GroupSubCommand.GROUP_RESOLUTION_DELEGATE.getAutoCompleteFor(args[0]);
    }
}
