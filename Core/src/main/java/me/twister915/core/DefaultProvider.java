package me.twister915.core;

import me.twister915.core.network.NetworkManager;
import me.twister915.core.network.lilypad.LilyPadNetworkManager;
import me.twister915.core.player.CPlayerManager;
import me.twister915.core.player.DatabaseConnectException;
import me.twister915.core.player.mongo.CMongoDatabase;
import me.twister915.core.player.mongo.CMongoPlayerManager;
import org.bukkit.configuration.file.FileConfiguration;

public final class DefaultProvider implements Provider {
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
}
