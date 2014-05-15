package net.communitycraft.core;

import lombok.Getter;
import net.communitycraft.core.config.YAMLConfigurationFile;
import net.communitycraft.core.modular.ModularPlugin;
import net.communitycraft.core.network.NetworkManager;
import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPlayer;
import net.communitycraft.core.player.CPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Core extends JavaPlugin {
    @Getter private static Core instance;
    @Getter private static Random random;

    private CPlayerManager playerManager;
    @Getter private NetworkManager networkManager;
    @Getter private YAMLConfigurationFile databaseConfiguration;
    @Getter private List<ModularPlugin> modules = new ArrayList<>();
    @Getter protected Provider defaultProvider = new DefaultProvider();

    @Override
    public final void onEnable() {
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

            //Talk to the provider and setup the database
            this.playerManager = defaultProvider.getNewPlayerManager(this);
            this.playerManager.getDatabase().connect();

            //Setup network manager through the provider as well
            this.networkManager = defaultProvider.getNewNetworkManager(this);
        } catch (Throwable t) {
            t.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public final void onDisable() {
        try {
            this.playerManager.onDisable();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        instance = null;
    }

    public final <T extends Listener> T registerListener(T listener) {
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

    public static NetworkManager getNetworkManager() {
        return instance.networkManager;
    }
}
