package net.cogzmc.core;

import lombok.Getter;
import lombok.NonNull;
import net.cogzmc.core.config.YAMLConfigurationFile;
import net.cogzmc.core.effect.TitleManager;
import net.cogzmc.core.effect.enderBar.EnderBarManager;
import net.cogzmc.core.effect.npc.SoftNPCManager;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.player.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

/**
 * This represents the very Core of the CC System.
 *
 * This will manage all Modules and also the Core Managers.
 *
 * The core managers includes the PlayerManager, NetworkManager, and ServiceManager.
 *
 * You can access instances of other modules by depending on them in your pom.xml, and then executing Core.get
 *
 * @author Joey Sacchini
 */
public class Core extends JavaPlugin {
    //**NEVER COMMIT THIS VALUE EQUALING TRUE**
    /*
     * This value may be set to true and should be implemented throughout the project
     * You may only, however, use it when you are locally compiling and locally testing, or you are on a branch of your own
     *
     * Do not ever commit this value as *true* to the master git branch of any project.
     *
     * This is the only debug variable that should be used throughout the project, although this is subject to change in the future.
     *
     * - Joey
     */

    /* READ THE WARNING ABOVE */ public final static boolean DEBUG = false; /* READ THE WARNING ABOVE */

    @Getter private static Core instance;
    @Getter private static Random random = new Random();

    private List<ModularPlugin> modules = new ArrayList<>();
    @Getter protected Provider provider;
    @Getter private CDatabase cDatabase;

    private CPlayerManager playerManager;
    private CPermissionsManager permissionsManager;
    private NetworkManager networkManager;
    private EnderBarManager enderBarManager;
    private TitleManager titleManager;

    @Getter private YAMLConfigurationFile databaseConfiguration;
    @Getter private Integer saveFrequency;
    @Getter private boolean hasProtocolLib;

    @Override
    public final void onEnable() {
        instance = this;
        hasProtocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
        try {
            saveDefaultConfig();
            //Kick any online players
            String kickMessage = ChatColor.translateAlternateColorCodes('&',getConfig().getString("kick-message"));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(kickMessage);
            }
            //Connect to the cDatabase
            databaseConfiguration = new YAMLConfigurationFile(this, "database.yml");
            databaseConfiguration.reloadConfig();
            databaseConfiguration.saveDefaultConfig();

            //Setup the provider
            try {
                provider = (Provider) Class.forName(getConfig().getString("provider")).newInstance();
            } catch (Exception e) {
                Core.logInfo("We were unable to load your custom provider. " + e.getMessage());
                if (DEBUG) e.printStackTrace();
                provider = new DefaultProvider();
            }

            //read some settings
            this.saveFrequency = getConfig().getInt("save-frequency", 60);

            //Setup everything.
            this.cDatabase = provider.getNewDatabase(this);
            this.cDatabase.connect();
            this.playerManager = provider.getNewPlayerManager(this);
            this.networkManager = provider.getNewNetworkManager(this);
            this.permissionsManager = provider.getNewPermissionsManager(this, this.playerManager);

            //Some extras
            if (hasProtocolLib) {
                this.enderBarManager = new EnderBarManager();
                this.titleManager = new TitleManager();
            }
            new SoftNPCManager();
            File geoIPDatabase = new File(getDataFolder(), getConfig().getString("geo-ip-database"));
            if (geoIPDatabase.exists()) {
                try {
                    this.playerManager.setupNewGeoIPManager(geoIPDatabase);
                } catch (Exception e) {
                    logInfo("Could not setup GeoIP Database!");
                    if (DEBUG) e.printStackTrace();
                    logDebug("-------------------------------");
                }
            }

            Bukkit.getPluginManager().registerEvents(new CPlayerJoinPrefixTagListener(),this);
        } catch (Throwable t) {
            t.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public final void onModulePreEnable(ModularPlugin modularPlugin) {
        this.modules.add(modularPlugin);
    }

    public final void onModulePreDisable(ModularPlugin modularPlugin) {
        this.modules.remove(modularPlugin);
    }

    @Override
    public final void onDisable() {
        try {
            if (this.playerManager != null) this.playerManager.onDisable();
            if (this.cDatabase != null) cDatabase.disconnect();
            if (this.networkManager != null) this.networkManager.onDisable();
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
        for (ModularPlugin module : modules)
            if (modularClass.equals(module.getClass())) //noinspection unchecked
                return (T) module;
        return null;
    }

    /**
     * Returns whether a module by the passed name exists / has been loaded
     * @param moduleTitle  Name of module to search for
     * @return  Whether the specified module exists
     */
    public boolean doesModuleExist(String moduleTitle){
        for(ModularPlugin module : modules)
            if (moduleTitle.equals(module.getName())) return true;
        return false;
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

    public static CPlayer getOnlinePlayer(@NonNull Player player) {
        return getPlayerManager().getCPlayerForPlayer(player);
    }

    public static COfflinePlayer getOfflinePlayerByUUID(UUID uuid) {
        return getPlayerManager().getOfflinePlayerByUUID(uuid);
    }

    public static void logInfo(String s) {
        instance.getLogger().info(s);
    }

    public static void logDebug(String s) {if (DEBUG) instance.getLogger().info(s);}

    public static CPlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public static NetworkManager getNetworkManager() {
        return instance.networkManager;
    }

    public static CPermissionsManager getPermissionsManager() {
        return instance.permissionsManager;
    }

    public static EnderBarManager getEnderBarManager() {return instance.enderBarManager;}

    public static TitleManager getTitleManager() { return instance.titleManager; }
}
