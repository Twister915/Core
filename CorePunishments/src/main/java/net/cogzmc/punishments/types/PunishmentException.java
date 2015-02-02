package net.cogzmc.punishments.types;

@EqualsAndHashCode(callSuper = true)
@Value
public class PunishmentException extends Exception {
    private final String message;
}
