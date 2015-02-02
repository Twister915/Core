package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("setdefault")
public final class GroupSetDefaultVerb extends Verb<CGroup> {
    private final String[] names = {"setdefault"};
    private final Integer requiredArguments = 0;

    @Override
    protected void perform(CommandSender sender, CGroup target, String[] args) throws CommandException {
        Core.getPermissionsManager().setDefaultGroup(target);
        sendSuccessMessage("Set default group to " + target.getName(), sender);
    }
}
