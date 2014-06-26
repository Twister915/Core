package net.cogzmc.core;

import lombok.Getter;
import net.cogzmc.core.config.YAMLConfigurationFile;
import net.cogzmc.core.effect.enderBar.EnderBarManager;
import net.cogzmc.core.effect.npc.SoftNPCManager;
import net.cogzmc.core.model.ModelManager;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.netfiles.NetFileManager;
import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.player.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
 *
 * @author Joey Sacchini
 */
public class Core extends JavaPlugin {
    @Getter private static Core instance;
    @Getter private static Random random = new Random();

    private List<ModularPlugin> modules = new ArrayList<>();
    @Getter protected Provider provider;
    @Getter private CDatabase cDatabase;

    private CPlayerManager playerManager;
    private CPermissionsManager permissionsManager;
    private NetworkManager networkManager;
    private NetFileManager netFileManager;
    private ModelManager modelManager;
    private EnderBarManager enderBarManager;

    @Getter private YAMLConfigurationFile databaseConfiguration;

    @Override
    public final void onEnable() {
        instance = this;
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
                provider = new DefaultProvider();
            }

            //Setup everything.
            this.cDatabase = provider.getNewDatabase(this);
            this.cDatabase.connect();
            this.playerManager = provider.getNewPlayerManager(this);
            this.networkManager = provider.getNewNetworkManager(this);
            this.permissionsManager = provider.getNewPermissionsManager(this, this.playerManager);
            this.netFileManager = provider.getNewNetFileManager(this);
            this.modelManager = provider.getNewModelManager(this);

            //Some extras
            this.enderBarManager = new EnderBarManager();
            new SoftNPCManager();
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

    public static CPlayer getOnlinePlayer(Player player) {
        return getPlayerManager().getCPlayerForPlayer(player);
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

    public static EnderBarManager getEnderBarManager() {return instance.enderBarManager;}
}
