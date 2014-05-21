package net.communitycraft.core;

import lombok.Getter;
import net.communitycraft.core.config.YAMLConfigurationFile;
import net.communitycraft.core.model.ModelManager;
import net.communitycraft.core.modular.ModularPlugin;
import net.communitycraft.core.netfiles.NetFileManager;
import net.communitycraft.core.network.NetworkManager;
import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPermissionsManager;
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

    @Getter private List<ModularPlugin> modules = new ArrayList<>();
    @Getter protected Provider provider;

    private CPlayerManager playerManager;
    private CPermissionsManager permissionsManager;
    private NetworkManager networkManager;
    private NetFileManager netFileManager;
    private ModelManager modelManager;

    @Getter private YAMLConfigurationFile databaseConfiguration;

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

            //Setup everything.
            this.playerManager = provider.getNewPlayerManager(this);
            this.networkManager = provider.getNewNetworkManager(this);
            this.permissionsManager = provider.getNewPermissionsManager(this, this.playerManager.getDatabase(), this.playerManager);
            this.netFileManager = provider.getNewNetFileManager(this);
            this.modelManager = provider.getNewModelManager(this.playerManager.getDatabase());
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

    public static CPlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public static NetworkManager getNetworkManager() {
        return instance.networkManager;
    }

    public static CPermissionsManager getPermissionsManager() {
        return instance.permissionsManager;
    }

    public static NetFileManager getNetFileManager() {return instance.netFileManager;}

    public static ModelManager getModelManager() {return instance.modelManager;}
}
