package net.cogzmc.permissions.command.impl.verbs;

import lombok.Getter;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.command.Verb;
import org.bukkit.command.CommandSender;

@Getter
public final class PermSetVerb<T extends CPermissible> extends Verb<T> {
    private final String[] names = new String[]{"set"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, T target, String[] args) throws CommandException {
        if (args.length < 1) throw new ArgumentRequirementException("You have not specified a permission to set!");
        boolean value = args.length == 1 || !args[1].equalsIgnoreCase("false");
        target.setPermission(args[0], value);
    }
}
