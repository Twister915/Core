package net.communitycraft.core.network;

import lombok.Data;

@Data
public class NetworkUpdaterTask implements Runnable {
    private final NetworkManager networkManager;
    @Override
    public void run() {
        networkManager.updateHeartbeat();
    }
}
