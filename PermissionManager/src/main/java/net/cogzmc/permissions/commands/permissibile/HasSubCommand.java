package net.cogzmc.permissions.commands.permissibile;

import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.PermissionsManager;
import net.cogzmc.permissions.commands.general.PermissibleSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class HasSubCommand<T extends CPermissible> extends PermissibleSubCommand<T> {

    private final PermissibleResolutionDelegate<T> resolutionDelegate;

    public HasSubCommand(PermissibleResolutionDelegate<T> resolutionDelegate) {
        super("has");
        this.resolutionDelegate = resolutionDelegate;
    }

    @Override
    protected T getPermissible(String name) {
        return this.resolutionDelegate.getFor(name);
    }

    @Override
    protected String getNameOfPermissibleType() {
        return this.resolutionDelegate.getNameOfType();
    }

    @Override
    protected boolean needsSecondArgument() {
        return true;
    }

    @Override
    protected List<String> getComplete(String arg) {
        return resolutionDelegate.getAutoCompleteFor(arg);
    }

    @Override
    public void doAction(T permissible, String permission, CommandSender sender) {
        boolean b = permissible.hasPermission(permission);
        String has = b ? "Yes" : "No";
        sender.sendMessage(PermissionsManager.getInstance().
                getFormat("has-permission",
                        new String[]{"<name>", permissible.getName()},
                        new String[]{"<permission>", permission},
                        new String[]{"<has>", b ? ChatColor.GREEN.toString() + has : ChatColor.RED.toString() + has}
                )
        );
    }
}
