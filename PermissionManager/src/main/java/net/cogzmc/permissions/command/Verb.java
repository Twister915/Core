package net.cogzmc.permissions.command;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CPermissible;
import org.bukkit.command.CommandSender;

public abstract class Verb<T extends CPermissible> {
    protected abstract void perform(CommandSender sender, T target, String[] args) throws CommandException;
    protected abstract String[] getNames();
    protected abstract Integer getRequiredArguments();
    protected boolean canAcceptNullTarget() {return false;}
}
