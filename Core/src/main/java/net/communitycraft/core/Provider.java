package net.communitycraft.core;

import net.communitycraft.core.network.NetworkManager;
import net.communitycraft.core.player.CPlayerManager;

interface Provider {
    CPlayerManager getNewPlayerManager(Core core);
    NetworkManager getNewNetworkManager(Core core);
}
