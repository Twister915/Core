package net.communitycraft.core;

import net.communitycraft.core.network.NetworkManager;
import net.communitycraft.core.player.CPlayerManager;
import net.communitycraft.core.player.DatabaseConnectException;

interface Provider {
    CPlayerManager getNewPlayerManager(Core core) throws DatabaseConnectException;
    NetworkManager getNewNetworkManager(Core core);
}
