package net.communitycraft.core.modular.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;

public class UnhandledCommandExceptionException extends CommandException {
    @Getter private final Exception causingException;
    public UnhandledCommandExceptionException(Exception e, ModuleCommand command, String[] args, CommandSender sender) {
        super("Unhandled exception " + e.getMessage(), command, args, sender);
        this.causingException = e;
    }
}
