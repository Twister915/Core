package net.communitycraft.core.modular.command;

import org.bukkit.command.CommandSender;

public final class PermissionException extends CommandException {
    public PermissionException(String message, ModuleCommand command, String[] args, CommandSender sender) {
        super(message, command, args, sender);
    }
}
