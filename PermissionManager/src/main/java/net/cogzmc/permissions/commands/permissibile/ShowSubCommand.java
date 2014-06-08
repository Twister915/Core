package net.cogzmc.permissions.commands.permissibile;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.PermissionsManager;
import net.cogzmc.permissions.commands.general.PermissibleSubCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ShowSubCommand<T extends CPermissible> extends PermissibleSubCommand<T> {

    private final PermissibleResolutionDelegate<T> resolutionDelegate;

    public ShowSubCommand(PermissibleResolutionDelegate<T> resolutionDelegate) {
        super("show");
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
    protected void doAction(T permissible, CommandSender sender) throws CommandException {
        sender.sendMessage("Name: " + permissible.getName());
        ChatColor prefixColor = ChatColor.getByChar(permissible.getChatPrefix());
        sender.sendMessage("Prefix: " + prefixColor + prefixColor.toString() + ChatColor.RESET);
        ChatColor suffixColor = ChatColor.getByChar(permissible.getChatSuffix());
        sender.sendMessage("Suffix: " + suffixColor + suffixColor.toString() + ChatColor.RESET);

        if (permissible instanceof CGroup) {
            CGroup cGroupPermissible = (CGroup) permissible;
            sender.sendMessage("Parents: " + StringUtils.join(cGroupPermissible.getParents(), ", "));
            sender.sendMessage("Priority: " + cGroupPermissible.getPriority());
        } else if (permissible instanceof COfflinePlayer) {
            COfflinePlayer cOfflinePlayerPermissible = (COfflinePlayer) permissible;
            sender.sendMessage("Groups: " + StringUtils.join(cOfflinePlayerPermissible.getGroups(), ", "));
        }

        sender.sendMessage("Tab Color: " + permissible.getTablistColor() + permissible.getTablistColor().toString() + ChatColor.RESET);
        sender.sendMessage("Chat Color: " + permissible.getChatColor() + permissible.getChatColor().toString() + ChatColor.RESET);
    }
}
