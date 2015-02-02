package net.cogzmc.permissions.command.impl.verbs;

import lombok.Getter;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;
import org.bukkit.command.CommandSender;

@Getter
@PermissionName("show")
public final class PermShowVerb<T extends CPermissible> extends Verb<T> {
    private final String[] names = {"show"};
    private final Integer requiredArguments = 0;

    @Override
    protected void perform(CommandSender sender, T target, String[] args) throws CommandException {

    }
}
