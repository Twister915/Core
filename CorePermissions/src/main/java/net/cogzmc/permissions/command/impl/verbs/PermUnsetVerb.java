package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("unset")
public final class PermUnsetVerb<T extends CPermissible> extends Verb<T> {
    private final String[] names = new String[]{"unset"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, T target, String[] args) throws CommandException {
        target.unsetPermission(args[0]);
        sendSuccessMessage("Unset permission " + args[0] + " for " + target.getName(), sender);
    }
}
