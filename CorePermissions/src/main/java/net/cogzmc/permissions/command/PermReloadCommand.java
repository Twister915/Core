package net.cogzmc.permissions.command;

import net.cogzmc.permissions.PermissionsManager;

@CommandPermission("core.permissions.reload")
public final class PermReloadCommand extends ModuleCommand {
    public PermReloadCommand() {
        super("reload");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Core.getPermissionsManager().reloadPermissions();
        if (sender instanceof Player) Core.getOnlinePlayer(((Player) sender)).playSoundForPlayer(Sound.LEVEL_UP);
        sender.sendMessage(PermissionsManager.getInstance().getFormat("permissions-reload"));
    }
}
