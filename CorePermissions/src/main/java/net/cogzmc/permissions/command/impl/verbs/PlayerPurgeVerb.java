package net.cogzmc.permissions.command.impl.verbs;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@Getter
@PermissionName("purge")
public final class PlayerPurgeVerb extends Verb<COfflinePlayer> {
    private final String[] names = new String[]{"purge"};
    private final Integer requiredArguments = 0;

    @Override
    protected void perform(CommandSender sender, COfflinePlayer target, String[] args) throws CommandException {
        if (target instanceof CPlayer) {
            ((CPlayer) target).getBukkitPlayer().kickPlayer(ChatColor.RED + "Your Core data is being purged!");
            target = Core.getOfflinePlayerByUUID(target.getUniqueIdentifier());
        }
        Core.getPlayerManager().deletePlayerRecords(target);
        sendSuccessMessage("Purged player " + target.getName(), sender);
    }
}
