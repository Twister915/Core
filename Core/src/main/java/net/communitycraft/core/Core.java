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

/**
 * This represents the very Core of the CC System.
 *
 * This will manage all Modules and also the Core Managers.
 *
 * The core managers includes the PlayerManager, NetworkManager, and ServiceManager.
 *
 * You can access instances of other modules by depending on them in your pom.xml, and then executing Core.get
 */
public class Core extends JavaPlugin {
    @Getter private static Core instance;
    @Getter private static Random random;

    private CPlayerManager playerManager;
    @Getter private NetworkManager networkManager;
    @Getter private YAMLConfigurationFile databaseConfiguration;
    @Getter private List<ModularPlugin> modules = new ArrayList<>();
    @Getter protected Provider provider;

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

            //Setup the provider
            try {
                provider = (Provider) Class.forName(getConfig().getString("provider")).newInstance();
            } catch (Exception e) {
                provider = new DefaultProvider();
            }

            //Talk to the provider and setup the database
            this.playerManager = provider.getNewPlayerManager(this);

            //Setup network manager through the provider as well
            this.networkManager = provider.getNewNetworkManager(this);
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

    public <T extends ModularPlugin> T getModuleProvider(Class<T> modularClass) {
        for (ModularPlugin module : modules) {
            if (modularClass.equals(module.getClass())) //noinspection unchecked
                return (T) module;
        }
        return null;
    }

    /* Public singleton methods!*/

    public static CPlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public static <T extends ModularPlugin> T getModule(Class<T> moduleClass) {
        return getInstance().getModuleProvider(moduleClass);
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
