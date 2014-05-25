package net.communitycraft.permissions.commands;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.communitycraft.permissions.PermissionsManager;
import net.communitycraft.permissions.PermissionsReloadNetCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class RefreshSubCommand extends ModuleCommand {
    public RefreshSubCommand() {
        super("refresh");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        boolean isGlobal = args.length > 1 && args[0].equalsIgnoreCase("network");
        Core.getPermissionsManager().reloadPermissions();
        if (isGlobal && Core.getNetworkManager() != null) Core.getNetworkManager().sendMassNetCommand(new PermissionsReloadNetCommand());
        sender.sendMessage(PermissionsManager.getInstance().getFormat("reloaded"));
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Arrays.asList("network");
    }
}
