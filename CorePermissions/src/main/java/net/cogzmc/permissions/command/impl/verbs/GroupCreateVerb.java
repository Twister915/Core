package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("create")
public final class GroupCreateVerb extends Verb<CGroup> {
    private final String[] names = new String[]{"create"};
    private final Integer requiredArguments = 0;

    @Override
    protected void perform(CommandSender sender, CGroup target, String[] args) throws CommandException {
        if (target != null) throw new ArgumentRequirementException("The group you are trying to create already exists!");
        CGroup newGroup = Core.getPermissionsManager().createNewGroup(args[0]);
        sendSuccessMessage("Created a new " + newGroup.getName() + " group!", sender);
    }

    @Override
    protected boolean canAcceptNullTarget() {
        return true;
    }
}
