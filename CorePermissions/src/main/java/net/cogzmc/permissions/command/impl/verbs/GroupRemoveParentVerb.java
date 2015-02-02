package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("remparent")
public final class GroupRemoveParentVerb extends Verb<CGroup> {
    private final String[] names = new String[]{"removeparent", "delparent"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, CGroup target, String[] args) throws CommandException {
        CGroup group = Core.getPermissionsManager().getGroup(args[0]);
        if (group == null) throw new ArgumentRequirementException("The group you specified is not a valid group!");
        target.removeParent(group);
        sendSuccessMessage("Removed " + group.getName() + " as a parent of " + target.getName(), sender);
    }
}
