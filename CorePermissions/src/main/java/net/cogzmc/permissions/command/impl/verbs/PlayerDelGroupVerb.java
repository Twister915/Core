package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("delgroup")
public  final class PlayerDelGroupVerb extends Verb<COfflinePlayer> {
    private final String[] names = new String[]{"delgroup", "remgroup"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, COfflinePlayer target, String[] args) throws CommandException {
        CGroup group = Core.getPermissionsManager().getGroup(args[0]);
        if (group == null) throw new ArgumentRequirementException("The group you specified is null!");
        target.removeFromGroup(group);
        sendSuccessMessage("Removed player " + target.getName() + " from " + group.getName(), sender);
    }
}
