package net.cogzmc.permissions.command;

import net.cogzmc.permissions.PermissionsManager;

public abstract class Verb<T extends CPermissible> {
    protected abstract void perform(CommandSender sender, T target, String[] args) throws CommandException;
    protected abstract String[] getNames();
    protected abstract Integer getRequiredArguments();
    protected boolean canAcceptNullTarget() {return false;}

    protected void sendSuccessMessage(String message, CommandSender sender) {
        if (sender instanceof Player) Core.getOnlinePlayer((Player) sender).playSoundForPlayer(Sound.NOTE_BASS);
        sender.sendMessage(PermissionsManager.getInstance().getFormat("success-message-command", new String[]{"<message>", message}));
    }
}
