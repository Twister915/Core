package net.cogzmc.core;

import net.cogzmc.core.model.ModelManager;
import net.cogzmc.core.model.mongo.MongoModelManager;
import net.cogzmc.core.netfiles.NetFileManager;
import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.network.lilypad.LilyPadNetworkManager;
import net.cogzmc.core.player.CDatabase;
import net.cogzmc.core.player.CPermissionsManager;
import net.cogzmc.core.player.CPlayerManager;
import net.cogzmc.core.player.DatabaseConnectException;
import net.cogzmc.core.player.mongo.CMongoDatabase;
import net.cogzmc.core.player.mongo.CMongoPermissionsManager;
import net.cogzmc.core.player.mongo.CMongoPlayerManager;
import org.bukkit.configuration.file.FileConfiguration;

final class DefaultProvider implements Provider {
    @Override
    public CPlayerManager getNewPlayerManager(Core core) throws DatabaseConnectException {
        FileConfiguration config = core.getDatabaseConfiguration().getConfig();
        //This will get the database values from the database.yml file
        CMongoDatabase mongoDatabase = new CMongoDatabase(
                config.getString("host", "127.0.0.1"),
                config.getInt("port", 28017),
                config.getString("database", "communitycraft"),
                config.getString("username", null),
                config.getString("password", null),
                config.getString("collectionPrefix")
        );
        return new CMongoPlayerManager(mongoDatabase);
    }

    @Override
    public NetworkManager getNewNetworkManager(Core core) {
        if (core.getServer().getPluginManager().getPlugin("LilyPad-Connect") != null)
            return new LilyPadNetworkManager();
        core.getLogger().severe("COULD NOT FIND A NETWORK PLUGIN TO CONNECT TO!");
        return null;
    }

    @Override
    public CPermissionsManager getNewPermissionsManager(Core core, CDatabase database, CPlayerManager playerManager) {
        return new CMongoPermissionsManager((CMongoDatabase) database, playerManager);
    }

    @Override
    public NetFileManager getNewNetFileManager(Core core) {
        return null;
    }

    @Override
    public ModelManager getNewModelManager(CDatabase database) {
        return new MongoModelManager((CMongoDatabase)database);
    }
}
