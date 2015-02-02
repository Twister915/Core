package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("set")
public final class PermSetVerb<T extends CPermissible> extends Verb<T> {
    private final String[] names = new String[]{"set"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, T target, String[] args) throws CommandException {
        if (args.length < 1) throw new ArgumentRequirementException("You have not specified a permission to set!");
        boolean value = args.length == 1 || !args[1].equalsIgnoreCase("false");
        target.setPermission(args[0], value);
        sendSuccessMessage("Set permission " + args[0] + " to " + (value ? "true" : "false") + " for " + target.getName(), sender);
    }
}
