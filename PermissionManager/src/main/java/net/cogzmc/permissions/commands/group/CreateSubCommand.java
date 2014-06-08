package net.cogzmc.permissions.commands.group;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.permissions.PermissionsManager;
import org.bukkit.command.CommandSender;

public class CreateSubCommand extends ModuleCommand {
    public CreateSubCommand() {
        super("create");
    }

    @Override
    public void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) throw new ArgumentRequirementException("You must supply a group name!");
        CGroup newGroup = Core.getPermissionsManager().createNewGroup(args[0]);
        sender.sendMessage(PermissionsManager.getInstance().getFormat("create-group", new String[]{"<group>", newGroup.getName()}));
    }
}
