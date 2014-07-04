package net.cogzmc.permissions.command.impl.verbs;

import lombok.Getter;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.permissions.command.Verb;
import org.bukkit.command.CommandSender;

@Getter
public final class GroupSetPriorityVerb extends Verb<CGroup> {
    private final String[] names = new String[]{"setpriority", "priority"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, CGroup target, String[] args) throws CommandException {
        target.setPriority(Integer.valueOf(args[0]));
    }
}
