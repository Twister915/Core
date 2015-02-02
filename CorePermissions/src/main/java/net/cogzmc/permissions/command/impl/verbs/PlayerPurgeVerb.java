package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("purge")
public final class PlayerPurgeVerb extends Verb<COfflinePlayer> {
    private final String[] names = new String[]{"purge"};
    private final Integer requiredArguments = 0;

    @Override
    protected void perform(CommandSender sender, COfflinePlayer target, String[] args) throws CommandException {
        if (target instanceof CPlayer) {
            ((CPlayer) target).kickPlayer(ChatColor.RED + "Your Core data is being purged!");
            target = Core.getOfflinePlayerByUUID(target.getUniqueIdentifier());
        }
        Core.getPlayerManager().deletePlayerRecords(target);
        sendSuccessMessage("Purged player " + target.getName(), sender);
    }
}
