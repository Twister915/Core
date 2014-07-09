package net.cogzmc.punishments.types;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class PunishmentException extends Exception {
    private final String message;
}
