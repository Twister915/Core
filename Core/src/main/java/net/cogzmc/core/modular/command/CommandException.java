package net.cogzmc.core.modular.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommandException extends Exception {
    private final String message;
}
