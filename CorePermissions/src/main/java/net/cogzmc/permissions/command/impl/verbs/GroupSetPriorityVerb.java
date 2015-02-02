package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("setpriority")
public final class GroupSetPriorityVerb extends Verb<CGroup> {
    private final String[] names = new String[]{"setpriority", "priority"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, CGroup target, String[] args) throws CommandException {
        target.setPriority(Integer.valueOf(args[0]));
        sendSuccessMessage("Set " + target.getName() + "'s priority to " + target.getPriority(), sender);
    }
}
