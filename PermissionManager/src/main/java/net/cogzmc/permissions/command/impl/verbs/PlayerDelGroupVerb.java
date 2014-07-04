package net.cogzmc.permissions.command.impl.verbs;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.permissions.command.Verb;
import org.bukkit.command.CommandSender;

@Getter
public  final class PlayerDelGroupVerb extends Verb<COfflinePlayer> {
    private final String[] names = new String[]{"delgroup", "remgroup"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, COfflinePlayer target, String[] args) throws CommandException {
        CGroup group = Core.getPermissionsManager().getGroup(args[0]);
        if (group == null) throw new ArgumentRequirementException("The group you specified is null!");
        target.removeFromGroup(group);
    }
}
