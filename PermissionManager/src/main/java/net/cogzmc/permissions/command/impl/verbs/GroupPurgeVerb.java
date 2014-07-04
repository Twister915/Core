package net.cogzmc.permissions.command.impl.verbs;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.DatabaseConnectException;
import net.cogzmc.permissions.command.Verb;
import org.bukkit.command.CommandSender;

@Getter
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
    }
}
