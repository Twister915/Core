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
     * Gets the number of players online for each {@link net.communitycraft.core.network.NetworkServer} that we are aware of.
     *
     * This method is especially useful for sorting data about which servers have the most online players.
     * @return A {@link java.util.Map} relating each individual {@link net.communitycraft.core.network.NetworkServer} to an {@link java.lang.Integer} denoting the number of players online on that particular server.
     */
    Map<NetworkServer, Integer> getOnlinePlayersPerServer();

    /**
     * Registers a handler for an incoming {@link net.communitycraft.core.network.NetCommand}.
     * @param handler The handler you wish to register.
     * @param type The type of {@link net.communitycraft.core.network.NetCommand} you wish to handle. This must agree with the type that the {@link net.communitycraft.core.network.NetCommandHandler} specified in the {@code handler} parameter.
     * @param <T> The type of {@link net.communitycraft.core.network.NetCommand} that both the {@code type} and {@code handler} agree upon.
     */
    <T extends NetCommand> void registerNetCommandHandler(NetCommandHandler<T> handler, Class<T> type);

    /**
     * Un-registers a handler for an incoming {@link net.communitycraft.core.network.NetCommand}
     * @param handler The handler to un-register for the {@link net.communitycraft.core.network.NetCommand}
     * @param type The type of {@link net.communitycraft.core.network.NetCommand} that the {@link net.communitycraft.core.network.NetCommandHandler} handles.
     * @param <T> The type parameter that forces agreement between the {@code type} and {@code handler}.
     */
    <T extends NetCommand> void unregisterHandler(NetCommandHandler<T> handler, Class<T> type);

    /**
     * Gets all {@link net.communitycraft.core.network.NetCommandHandler}s for a specific {@link net.communitycraft.core.network.NetCommand} type.
     * @param type The type that the target {@link net.communitycraft.core.network.NetCommandHandler}s handle.
     * @param <T> The type parameter used to discover said {@link net.communitycraft.core.network.NetCommandHandler}s.
     * @return {@link java.util.List} of all discovered {@link net.communitycraft.core.network.NetCommandHandler}s.
     */
    <T extends NetCommand> List<NetCommandHandler<T>> getNetCommandHandlersFor(Class<T> type);

    /**
     * Sends a mass {@link net.communitycraft.core.network.NetCommand}. The contents of this command will, as expected appear to originate from this server. The contents of this command will also be targeted at every other server on the network, regardless of our knowledge of the servers.
     * @param command The {@link net.communitycraft.core.network.NetCommand} with data intended to be sent out.
     */
    void sendMassNetCommand(NetCommand command);
}
