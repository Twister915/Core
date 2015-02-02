package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("prefix")
public final class PermPrefixVerb<T extends CPermissible> extends Verb<T> {
    private final String[] names = new String[]{"prefix"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, T target, String[] args) throws CommandException {
        target.setChatPrefix(Joiner.on(' ').join(args));
        sendSuccessMessage("Changed prefix to " + target.getChatPrefix() + "!", sender);
    }
}
