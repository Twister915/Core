package net.communitycraft.core.network;

import net.communitycraft.core.player.COfflinePlayer;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Server information master controller. This class gives you access to server information, allows you to search for servers, list all servers, and so on.
 */
public interface NetworkManager {
    /**
     * Gives you all known {@link net.communitycraft.core.network.NetworkServer}s on the network. Not all {@link net.communitycraft.core.network.NetworkServer}s may have been discovered since the server turned on.
     *
     * This will include the current server, but not based off a heart beat, rather it will pull from information from the player manger.
     *
     * @return All known {@link net.communitycraft.core.network.NetworkServer}s in a {@link List}.
     */
    List<NetworkServer> getServers();

    /**
     * Will find any server who's name matches the regex pattern provided.
     * @param regex A {@link java.util.regex.Pattern} object that represents a specific regex pattern.
     * @return Any servers that match the regex in a {@link java.util.List}
     */
    List<NetworkServer> getServersMatchingRegex(Pattern regex);

    /**
     * Will find any server who's name matches the regex pattern provided.
     * @param regex A {@link java.lang.String} object that contains a regex pattern.
     * @return Any servers that match the regex in a {@link java.util.List}
     */
    List<NetworkServer> getServersMatchingRegex(String regex);

    /**
     * Gets a {@link net.communitycraft.core.network.NetworkServer} by name.
     * @param name The name of the {@link net.communitycraft.core.network.NetworkServer} you are looking for.
     * @return A {@link net.communitycraft.core.network.NetworkServer} if there is one by that name, {@code null} if there is none.
     */
    NetworkServer getServer(String name);

    /**
     * Gets the {@link net.communitycraft.core.network.NetworkServer} that represents this server.
     * @return The {@link net.communitycraft.core.network.NetworkServer} that represents this server.
     */
    NetworkServer getThisServer();

    /**
     * This method is called by a timer automatically in the core to update the server data on an interval. This is basically a heartbeat for the {@link net.communitycraft.core.network.NetworkManager} and can be used to manually validate and update any cached data.
     */
    void updateHeartbeat();

    /**
     * Gets the total number of players online across all servers (including the current server) combined.
     * @return The number of players online globally.
     */
    Integer getTotalOnlineCount();

    /**
     * Gets an {@link java.util.List} of {@link net.communitycraft.core.player.COfflinePlayer} objects for all players online on the network globally (including this server).
     * @return The players online globally.
     */
    List<COfflinePlayer> getTotalPlayersOnline();

    /**
     *
     * @return
     */
    Map<NetworkServer, Integer> getOnlinePlayersPerServer();

    /**
     *
     * @param handler
     * @param type
     * @param <T>
     */
    <T extends NetCommand> void registerNetCommandHandler(NetCommandHandler<T> handler, Class<T> type);

    /**
     *
     * @param handler
     * @param type
     * @param <T>
     */
    <T extends NetCommand> void unregisterHandler(NetCommandHandler<T> handler, Class<T> type);

    <T extends NetCommand> List<NetCommandHandler<T>> getNetCommandHandlersFor(Class<T> type);
}
