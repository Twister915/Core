package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("setparent")
public final class GroupSetParentVerb extends Verb<CGroup> {
    private final String[] names = new String[]{"setparent"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, CGroup target, String[] args) throws CommandException {
        CGroup group = Core.getPermissionsManager().getGroup(args[0]);
        if (group == null) throw new ArgumentRequirementException("The parent you specified is not valid!");
        target.addParent(group);
        sendSuccessMessage("Set " + group.getName() + " as a parent of " + target.getName(), sender);
    }
}
