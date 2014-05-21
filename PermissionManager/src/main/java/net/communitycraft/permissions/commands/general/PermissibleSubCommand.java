package net.communitycraft.permissions.commands.general;

import net.communitycraft.core.modular.command.ArgumentRequirementException;
import net.communitycraft.core.modular.command.CommandException;
import net.communitycraft.core.modular.command.EmptyHandlerException;
import net.communitycraft.core.modular.command.ModuleCommand;
import net.communitycraft.core.player.CPermissible;
import net.communitycraft.permissions.PermissionsManager;
import org.bukkit.command.CommandSender;

public abstract class PermissibleSubCommand<PermissibleType extends CPermissible> extends ModuleCommand {
    protected PermissibleSubCommand(String name) {
        super(name);
    }

    @Override
    public final void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        //Check argument lengths
        if (args.length < 1 + (needsSecondArgument() ? 1 : 0)) throw new ArgumentRequirementException("You must specify enough arguments for this command!");
        //Get the permissible
        PermissibleType permissible = getPermissible(args[0]);
        if (permissible == null) throw new ArgumentRequirementException("The argument you specified is not a valid " + getNameOfPermissibleType());
        if (needsSecondArgument()) {
            String arg = args[1];
            if (!validateArgument(arg)) throw new ArgumentRequirementException("The argument you passed is not valid!");
            doAction(permissible, arg);
        } else {
            doAction(permissible);
        }
        sender.sendMessage(getSuccessMessage());
    }

    protected abstract PermissibleType getPermissible(String name);
    protected abstract String getNameOfPermissibleType();
    protected abstract boolean needsSecondArgument();

    protected void doAction(PermissibleType permissible, String argument) throws CommandException {throw new EmptyHandlerException();}
    protected void doAction(PermissibleType permissible) throws CommandException {throw new EmptyHandlerException();}
    protected boolean validateArgument(String argument) {return true;}
    protected String getSuccessMessage() {return PermissionsManager.getInstance().getFormat("success-command");}
}
