package me.twister915.core.network;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Server information master controller. This class gives you access to server information, allows you to search for servers, list all servers, and so on.
 */
public interface NetworkManager {
    /**
     * Gives you all known {@link me.twister915.core.network.NetworkServer}s on the network. Not all {@link me.twister915.core.network.NetworkServer}s may have been discovered since the server turned on.
     * @return All known {@link me.twister915.core.network.NetworkServer}s in a {@link List}.
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
     * Gets a {@link me.twister915.core.network.NetworkServer} by name.
     * @param name The name of the {@link me.twister915.core.network.NetworkServer} you are looking for.
     * @return A {@link me.twister915.core.network.NetworkServer} if there is one by that name, {@code null} if there is none.
     */
    NetworkServer getServer(String name);

    /**
     * This method is called by a timer automatically in the core to update the server data on an interval. This is basically a heartbeat for the {@link me.twister915.core.network.NetworkManager} and can be used to manually validate and update any cached data.
     */
    void updateHeartbeat();
}
