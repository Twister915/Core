package net.cogzmc.core.player;

@EqualsAndHashCode(callSuper = false)
@Data
public final class DatabaseConnectException extends Exception {
    private final String message;
    private final Exception cause;
    private final CDatabase database;
}
