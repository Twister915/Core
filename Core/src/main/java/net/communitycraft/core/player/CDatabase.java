package net.communitycraft.core.player;

public interface CDatabase {
    void connect() throws DatabaseConnectException;
    void disconnect() throws DatabaseConnectException;
}
