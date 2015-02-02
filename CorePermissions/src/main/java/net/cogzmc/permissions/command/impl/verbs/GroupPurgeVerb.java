package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("purge")
public final class GroupPurgeVerb extends Verb<CGroup> {
    private final String[] names = new String[]{"purge"};
    private final Integer requiredArguments = 0;

    @Override
    protected void perform(CommandSender sender, CGroup target, String[] args) throws CommandException {
        try {
            Core.getPermissionsManager().deleteGroup(target);
        } catch (DatabaseConnectException e) {
            e.printStackTrace();
            throw new CommandException("Could not execute due to error! " + e.getMessage());
        }
        sendSuccessMessage("Deleted the " + target.getName() + " group!", sender);
    }
}
