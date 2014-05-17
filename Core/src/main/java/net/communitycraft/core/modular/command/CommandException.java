package net.communitycraft.core.modular.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class CommandException extends Exception {
    private final String message;
}
