package net.cogzmc.core;

import net.cogzmc.core.model.ModelManager;
import net.cogzmc.core.netfiles.NetFileManager;
import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.player.CDatabase;
import net.cogzmc.core.player.CPermissionsManager;
import net.cogzmc.core.player.CPlayerManager;
import net.cogzmc.core.player.DatabaseConnectException;

public interface Provider {
    CDatabase getNewDatabase(Core core) throws Exception;
    CPlayerManager getNewPlayerManager(Core core) throws Exception;
    NetworkManager getNewNetworkManager(Core core) throws Exception;
    CPermissionsManager getNewPermissionsManager(Core core, CPlayerManager playerManager) throws Exception;
    NetFileManager getNewNetFileManager(Core core) throws Exception;
    ModelManager getNewModelManager(Core core) throws Exception;
}
