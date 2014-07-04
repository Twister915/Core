package net.cogzmc.permissions.command.impl.verbs;

import lombok.Getter;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.permissions.command.Verb;
import org.bukkit.command.CommandSender;

@Getter
public class PlayerSetGroupVerb extends Verb<COfflinePlayer> {
    private final String[] names = new String[]{"setgroup"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, COfflinePlayer target, String[] args) throws CommandException {

    }
}
