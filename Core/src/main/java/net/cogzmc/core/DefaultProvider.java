package net.cogzmc.core;

import net.cogzmc.core.config.YAMLConfigurationFile;
import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.network.bungee.BungeeCordNetworkManager;
import net.cogzmc.core.network.lilypad.LilyPadNetworkManager;
import net.cogzmc.core.player.CPermissionsManager;
import net.cogzmc.core.player.CPlayerManager;
import net.cogzmc.core.player.mongo.CMongoPermissionsManager;
import net.cogzmc.core.player.mongo.CMongoPlayerManager;

final class DefaultProvider implements Provider {
    @Override
    public CDatabase getNewDatabase(Core core) throws DatabaseConnectException {
        FileConfiguration config = core.getDatabaseConfiguration().getConfig();
        //This will get the database values from the database.yml file
        return new CMongoDatabase(
                config.getString("host", "127.0.0.1"),
                config.getInt("port", 28017),
                config.getString("database", "communitycraft"),
                config.getString("username", null),
                config.getString("password", null),
                config.getString("collectionPrefix")
        );
    }

    @Override
    public CPlayerManager getNewPlayerManager(Core core){
        //This will get the database values from the database.yml file
        return new CMongoPlayerManager((CMongoDatabase) Core.getInstance().getCDatabase());
    }

    @Override
    public NetworkManager getNewNetworkManager(Core core) throws Exception{
        if (core.getServer().getPluginManager().getPlugin(core.getConfig().getString("lilypad-plugin")) != null)
            return new LilyPadNetworkManager();
        else if (core.getConfig().getBoolean("use-bungeecord")) {
            YAMLConfigurationFile bungeeConfig = new YAMLConfigurationFile(core, "bungee.yml");
            bungeeConfig.reloadConfig();
            bungeeConfig.saveDefaultConfig();
            return new BungeeCordNetworkManager(bungeeConfig);
        }
        core.getLogger().severe("COULD NOT FIND A NETWORK PLUGIN TO CONNECT TO!");
        return null;
    }

    @Override
    public CPermissionsManager getNewPermissionsManager(Core core, CPlayerManager playerManager) {
        CMongoPlayerManager playerManager1 = (CMongoPlayerManager) playerManager;
        CMongoPermissionsManager cMongoPermissionsManager = new CMongoPermissionsManager((CMongoDatabase) core.getCDatabase(), playerManager1);
        playerManager1.setGroupRepository(cMongoPermissionsManager);
        return cMongoPermissionsManager;
    }
}
