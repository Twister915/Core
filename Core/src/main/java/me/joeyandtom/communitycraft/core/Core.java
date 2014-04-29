package me.joeyandtom.communitycraft.core;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.joeyandtom.communitycraft.core.config.YAMLConfigurationFile;
import me.joeyandtom.communitycraft.core.player.CDatabase;
import me.joeyandtom.communitycraft.core.player.CPlayerManager;
import me.joeyandtom.communitycraft.core.player.mongo.CMongoDatabase;
import me.joeyandtom.communitycraft.core.player.mongo.CMongoPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public final class Core extends JavaPlugin {
    @Getter private static Core instance;

    @Getter(AccessLevel.NONE) private final List<ModularPlugin> modularPlugins;

    private CPlayerManager playerManager;
    private YAMLConfigurationFile databaseConfiguration;

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
            CMongoDatabase mongoDatabase = new CMongoDatabase(
                    config.getString("host", "127.0.0.1"),
                    config.getInt("port", 28017),
                    config.getString("database", "communitycraft"),
                    config.getString("collectionPrefix")
            );
            this.playerManager = new CMongoPlayerManager(mongoDatabase);
            CDatabase database = this.playerManager.getDatabase();
            database.connect();
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
}
