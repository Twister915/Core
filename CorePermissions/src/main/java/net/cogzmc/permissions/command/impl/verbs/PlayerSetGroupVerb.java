package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("setgroup")
public final class PlayerSetGroupVerb extends Verb<COfflinePlayer> {
    private final String[] names = new String[]{"setgroup"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, COfflinePlayer target, String[] args) throws CommandException {
        CGroup group = Core.getPermissionsManager().getGroup(args[0]);
        if (group == null) throw new ArgumentRequirementException("The group you specified is invalid!");
        for (CGroup cGroup : target.getGroups()) {
            target.removeFromGroup(cGroup);
        }
        target.addToGroup(group);
        sendSuccessMessage("Set " + target.getName() +"'s only group to " + group.getName(), sender);
    }
}
