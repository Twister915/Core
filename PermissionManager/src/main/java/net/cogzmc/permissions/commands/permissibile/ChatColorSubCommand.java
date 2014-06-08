package net.cogzmc.permissions.commands.permissibile;

import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.commands.general.PermissibleSubCommand;
import org.bukkit.ChatColor;

import java.util.List;

public final class ChatColorSubCommand<T extends CPermissible> extends PermissibleSubCommand<T> {
    private final PermissibleResolutionDelegate<T> permissibleResolutionDelegate;

    public ChatColorSubCommand(PermissibleResolutionDelegate<T> permissibleResolutionDelegate) {
        super("chatcolor");
        this.permissibleResolutionDelegate = permissibleResolutionDelegate;
    }

    @Override
    protected T getPermissible(String name) {
        return permissibleResolutionDelegate.getFor(name);
    }

    @Override
    protected String getNameOfPermissibleType() {
        return permissibleResolutionDelegate.getNameOfType();
    }

    @Override
    protected boolean needsSecondArgument() {
        return true;
    }

    @Override
    protected List<String> getComplete(String arg) {
        return permissibleResolutionDelegate.getAutoCompleteFor(arg);
    }

    @Override
    protected void doAction(T permissible, String argument) {
        permissible.setChatColor(ChatColor.getByChar(argument.toCharArray()[1]));
    }

    @Override
    protected boolean validateArgument(String arg) {
        return arg.startsWith("&") && arg.length() == 2 && ChatColor.getByChar(arg.toCharArray()[1]) != null;
    }
}
