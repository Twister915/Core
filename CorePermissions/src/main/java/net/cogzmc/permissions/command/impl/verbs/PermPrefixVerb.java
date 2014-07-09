package net.cogzmc.permissions.command.impl.verbs;

import com.google.common.base.Joiner;
import lombok.Getter;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;
import org.bukkit.command.CommandSender;

@Getter
@PermissionName("prefix")
public final class PermPrefixVerb<T extends CPermissible> extends Verb<T> {
    private final String[] names = new String[]{"prefix"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, T target, String[] args) throws CommandException {
        target.setChatPrefix(Joiner.on(' ').join(args));
    }
}
