package net.communitycraft.core.network;

import java.util.List;

/**
 * Communicates with other servers on the network, and the network as a whole.
 */
public interface NetworkManager {
    List<NetworkServer> getServers();
    NetworkServer getServer(String name);

    /**
     * This method is called by a timer automatically in the core to update the server data on an interval. This is basically a heartbeat for the {@link net.communitycraft.core.network.NetworkManager} and can be used to manually validate and update any cached data.
     */
    void updateHeartbeat();
}
