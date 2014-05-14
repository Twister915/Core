package net.communitycraft.core;

import lombok.*;
import net.communitycraft.core.config.YAMLConfigurationFile;
import net.communitycraft.core.modular.ModularPlugin;
import net.communitycraft.core.player.CDatabase;
import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPlayer;
import net.communitycraft.core.player.CPlayerManager;
import net.communitycraft.core.player.mongo.CMongoDatabase;
import net.communitycraft.core.player.mongo.CMongoPlayerManager;
import net.communitycraft.core.network.NetworkManager;
import net.communitycraft.core.network.lilypad.LilyPadNetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Core extends JavaPlugin {
    @Getter private static Core instance;
    @Getter private static Random random;

    private CPlayerManager playerManager;
    @Getter private NetworkManager networkManager;
    @Getter  private YAMLConfigurationFile databaseConfiguration;

    @Getter private List<ModularPlugin> modules;

    @Override
    public void onEnable() {
        instance = this;
        try {
            saveDefaultConfig();
            //Kick any online players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer("CORE KICK");
            }
            //Connect to the database
            databaseConfiguration = new YAMLConfigurationFile(this, "database.yml");
            databaseConfiguration.reloadConfig();
            databaseConfiguration.saveDefaultConfig();
            FileConfiguration config = databaseConfiguration.getConfig();
            //This will get the database values from the database.yml file
            CMongoDatabase mongoDatabase = new CMongoDatabase(
                    config.getString("host", "127.0.0.1"),
                    config.getInt("port", 28017),
                    config.getString("database", "communitycraft"),
                    config.getString("username", null),
                    config.getString("password", null),
                    config.getString("collectionPrefix")
            );
            this.playerManager = new CMongoPlayerManager(mongoDatabase);
            CDatabase database = this.playerManager.getDatabase();
            database.connect();

            //Setup network manager
            if (getServer().getPluginManager().getPlugin("LilyPad-Connect") != null)
                this.networkManager = new LilyPadNetworkManager();
            else getLogger().severe("COULD NOT FIND A NETWORK PLUGIN TO CONNECT TO!");

            //Setup module list
            this.modules = new ArrayList<>();
        } catch (Throwable t) {
            t.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            this.playerManager.onDisable();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        instance = null;
    }

    public <T extends Listener> T registerListener(T listener) {
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    /* Public singleton methods!*/

    public static CPlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public static Collection<CPlayer> getOnlinePlayers() {
        return getPlayerManager().getOnlinePlayers();
    }

    public static CPlayer getOnlinePlayerByName(String name) {
        return getPlayerManager().getOnlineCPlayerForName(name);
    }

    public static COfflinePlayer getOfflinePlayerByUUID(UUID uuid) {
        return getPlayerManager().getOfflinePlayerByUUID(uuid);
    }

    public static void logInfo(String s) {
        instance.getLogger().info(s);
    }
}
