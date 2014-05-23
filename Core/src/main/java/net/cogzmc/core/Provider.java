package net.cogzmc.core;

import net.cogzmc.core.model.ModelManager;
import net.cogzmc.core.netfiles.NetFileManager;
import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.player.CDatabase;
import net.cogzmc.core.player.CPermissionsManager;
import net.cogzmc.core.player.CPlayerManager;
import net.cogzmc.core.player.DatabaseConnectException;

public interface Provider {
    CPlayerManager getNewPlayerManager(Core core) throws DatabaseConnectException;
    NetworkManager getNewNetworkManager(Core core);
    CPermissionsManager getNewPermissionsManager(Core core, CDatabase database, CPlayerManager playerManager);
    NetFileManager getNewNetFileManager(Core core);
    ModelManager getNewModelManager(CDatabase database);
}
