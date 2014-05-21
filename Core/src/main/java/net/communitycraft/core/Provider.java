package net.communitycraft.core;

import net.communitycraft.core.model.ModelManager;
import net.communitycraft.core.netfiles.NetFileManager;
import net.communitycraft.core.network.NetworkManager;
import net.communitycraft.core.player.CDatabase;
import net.communitycraft.core.player.CPermissionsManager;
import net.communitycraft.core.player.CPlayerManager;
import net.communitycraft.core.player.DatabaseConnectException;

interface Provider<Database extends CDatabase> {
    CPlayerManager getNewPlayerManager(Core core) throws DatabaseConnectException;
    NetworkManager getNewNetworkManager(Core core);
    CPermissionsManager getNewPermissionsManager(Core core, Database database, CPlayerManager playerManager);
    NetFileManager getNewNetFileManager(Core core);
    ModelManager<Database> getNewModelManager(Database database);
}
