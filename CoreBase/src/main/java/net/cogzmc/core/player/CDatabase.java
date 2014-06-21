package net.cogzmc.core.player;

/**
 * Serves to represent a database connection, and cannot provide more than that.
 *
 * @author Joey
 * @since 1.0
 */
public interface CDatabase {
    void connect() throws DatabaseConnectException;
    void disconnect() throws DatabaseConnectException;
}
