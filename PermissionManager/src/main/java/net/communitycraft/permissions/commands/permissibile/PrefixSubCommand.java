package net.communitycraft.permissions.commands.permissibile;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CPermissible;
import net.communitycraft.permissions.commands.general.PermissibleSubCommand;
import org.bukkit.ChatColor;

import java.util.List;

public final class PrefixSubCommand<T extends CPermissible> extends PermissibleSubCommand<T> {
    private final PermissibleResolutionDelegate<T> delegate;
    public PrefixSubCommand(PermissibleResolutionDelegate<T> delegate) {
        super("prefix");
        this.delegate = delegate;
    }


    @Override
    protected T getPermissible(String name) {
        return this.delegate.getFor(name);
    }

    @Override
    protected String getNameOfPermissibleType() {
        return this.delegate.getNameOfType();
    }

    @Override
    protected boolean needsSecondArgument() {
        return true;
    }

    @Override
    protected List<String> getComplete(String arg) {
        return this.delegate.getAutoCompleteFor(arg);
    }

    @Override
    protected void doAction(T permissible, String argument) throws CommandException {
        permissible.setChatPrefix(argument.equals("NaN") ? "" : ChatColor.translateAlternateColorCodes('&', argument));
    }
}
