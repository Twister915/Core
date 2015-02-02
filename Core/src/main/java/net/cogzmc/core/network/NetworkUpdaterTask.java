package net.cogzmc.core.network;

import lombok.Data;

@Data
/**
 * This is the task that is run by any NetworkManager implementation that will update the information in a Network and it's cache on a periodical basis.
 */
public final class NetworkUpdaterTask implements Runnable {
    private final NetworkManager networkManager;
    @Override
    public void run() {
        networkManager.updateHeartbeat();
    }
}
