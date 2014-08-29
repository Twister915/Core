package net.cogzmc.core;

import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.player.CDatabase;
import net.cogzmc.core.player.CPermissionsManager;
import net.cogzmc.core.player.CPlayerManager;

public interface Provider {
    CDatabase getNewDatabase(Core core) throws Exception;
    CPlayerManager getNewPlayerManager(Core core) throws Exception;
    NetworkManager getNewNetworkManager(Core core) throws Exception;
    CPermissionsManager getNewPermissionsManager(Core core, CPlayerManager playerManager) throws Exception;
}
