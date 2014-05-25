package net.communitycraft.permissions.commands.general;

import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.EmptyHandlerException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPermissible;
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
        boolean tookControlOfMessage = false;
        if (needsSecondArgument()) {
            String arg = args[1];
            if (!validateArgument(arg)) throw new ArgumentRequirementException("The argument you passed is not valid!");
            try {
                doAction(permissible, arg);
            } catch (EmptyHandlerException e) {
                doAction(permissible, arg, sender);
                tookControlOfMessage = true;
            }
            if (!tookControlOfMessage) sender.sendMessage(getSuccessMessage(permissible, arg));
        } else {
            try {
                doAction(permissible);
            } catch (EmptyHandlerException e) {
                doAction(permissible, sender);
                tookControlOfMessage = true;
            }
            if (!tookControlOfMessage) sender.sendMessage(getSuccessMessage(permissible));
        }
    }

    protected abstract PermissibleType getPermissible(String name);
    protected abstract String getNameOfPermissibleType();
    protected abstract boolean needsSecondArgument();

    protected void doAction(PermissibleType permissible, String argument) throws CommandException {throw new EmptyHandlerException();}
    protected void doAction(PermissibleType permissible) throws CommandException {throw new EmptyHandlerException();}

    protected void doAction(PermissibleType permissible, String argument, CommandSender sender) throws CommandException {throw new EmptyHandlerException();}
    protected void doAction(PermissibleType permissible, CommandSender sender) throws CommandException {throw new EmptyHandlerException();}

    protected boolean validateArgument(String argument) {return true;}
    protected String getSuccessMessage(PermissibleType target) {return PermissionsManager.getInstance().getFormat("success-command");}
    protected String getSuccessMessage(PermissibleType target, String argument) {return PermissionsManager.getInstance().getFormat("success-command");}
}
