package net.communitycraft.core.modular.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.command.CommandSender;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class CommandException extends Exception {
    private final String message;
    private final ModuleCommand command;
    private final String[] args;
    private final CommandSender sender;
}
