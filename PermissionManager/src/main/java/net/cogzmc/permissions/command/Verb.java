package net.cogzmc.permissions.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.PermissionsManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Verb<T extends CPermissible> {
    protected abstract void perform(CommandSender sender, T target, String[] args) throws CommandException;
    protected abstract String[] getNames();
    protected abstract Integer getRequiredArguments();
    protected boolean canAcceptNullTarget() {return false;}

    protected void sendSuccessMessage(String message, CommandSender sender) {
        if (sender instanceof Player) Core.getOnlinePlayer((Player) sender).playSoundForPlayer(Sound.NOTE_BASS);
        PermissionsManager.getInstance().getFormat("success-message-command", new String[]{"<message>", message});
    }
}
