package net.communitycraft.permissions.commands.permissibile;

import net.cogzmc.core.player.CPermissible;
import net.communitycraft.permissions.PermissionsManager;
import net.communitycraft.permissions.commands.general.PermissibleSubCommand;

import java.util.List;

public final class SetSubCommand<T extends CPermissible> extends PermissibleSubCommand<T> {
    private final PermissibleResolutionDelegate<T> permissibleResolutionDelegate;

    public SetSubCommand(PermissibleResolutionDelegate<T> permissibleResolutionDelegate) {
        super("set");
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
        String permission = argument;
        Boolean value = true;
        if (argument.contains("=")) {
            String[] split = argument.split("=");
            if (split.length == 2) {
                permission = split[0];
                value = !split[1].equalsIgnoreCase("false");
            }
        }
        permissible.setPermission(permission, value);
    }

    @Override
    protected String getSuccessMessage(T target, String argument) {
        String permission = argument;
        Boolean value = true;
        if (argument.contains("=")) {
            String[] split = argument.split("=");
            if (split.length == 2) {
                permission = split[0];
                value = !split[1].equalsIgnoreCase("false");
            }
        }
        return PermissionsManager.getInstance().getFormat("set-permission", new String[]{"<permission>", permission}, new String[]{"<value>", value.toString()});
    }
}
