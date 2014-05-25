package net.communitycraft.permissions.commands.permissibile;

import net.cogzmc.core.player.CPermissible;
import net.communitycraft.permissions.PermissionsManager;
import net.communitycraft.permissions.commands.general.PermissibleSubCommand;

public final class UnsetSubCommand<T extends CPermissible> extends PermissibleSubCommand<T> {
    private PermissibleResolutionDelegate<T> permissibleResolutionDelegate;
    protected UnsetSubCommand(PermissibleResolutionDelegate<T> delegate) {
        super("unset");
        permissibleResolutionDelegate = delegate;
    }

    @Override
    protected T getPermissible(String name) {
        return this.permissibleResolutionDelegate.getFor(name);
    }

    @Override
    protected String getNameOfPermissibleType() {
        return this.permissibleResolutionDelegate.getNameOfType();
    }

    @Override
    protected boolean needsSecondArgument() {
        return true;
    }

    @Override
    public void doAction(T target, String argument) {
        target.unsetPermission(argument);
    }

    @Override
    public String getSuccessMessage(T target, String argument) {
        return PermissionsManager.getInstance().getFormat("unset-permission", new String[]{"<target>", target.getName()}, new String[]{"<permission>", argument});
    }
}
