package net.cogzmc.permissions.command.impl.verbs;

import com.google.common.base.Joiner;
import lombok.Getter;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;
import org.bukkit.command.CommandSender;

@Getter
@PermissionName("suffix")
public final class PermSuffixVerb<T extends CPermissible> extends Verb<T> {
    private final String[] names = new String[]{"suffix"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, T target, String[] args) throws CommandException {
        String join = Joiner.on(' ').join(args);
        target.setChatSuffix(join);
        sendSuccessMessage("Set suffix to " + join + " for " + target.getName(), sender);
    }
}
