package net.cogzmc.permissions.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.permissions.PermissionsManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
