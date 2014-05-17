package me.twister915.core;

import me.twister915.core.network.NetworkManager;
import me.twister915.core.player.CPlayerManager;
import me.twister915.core.player.DatabaseConnectException;

interface Provider {
    CPlayerManager getNewPlayerManager(Core core) throws DatabaseConnectException;
    NetworkManager getNewNetworkManager(Core core);
}
