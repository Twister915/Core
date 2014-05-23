package net.cogzmc.core.player;

public interface CDatabase {
    void connect() throws DatabaseConnectException;
    void disconnect() throws DatabaseConnectException;
}
