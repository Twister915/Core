package net.cogzmc.permissions.command.impl.verbs;

import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.PermissionName;

@Getter
@PermissionName("chatcolor")
public final class PermChatColorVerb<T extends CPermissible> extends Verb<T> {
    private final String[] names = new String[]{"chatcolor"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, T target, String[] args) throws CommandException {
        String join = Joiner.on(' ').join(args);
        target.setChatColor(join);
        sendSuccessMessage("Set chat color to " + join + " for " + target.getName(), sender);
    }
}
