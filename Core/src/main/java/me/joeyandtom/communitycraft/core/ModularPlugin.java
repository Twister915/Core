package me.joeyandtom.communitycraft.core;

import me.joeyandtom.communitycraft.core.config.YAMLConfigurationFile;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModularPlugin extends JavaPlugin {

    private YAMLConfigurationFile formatsFile;

    @Override
    public void onEnable() {
        try {
            if (!Core.getInstance().isEnabled()) onFailureToEnable();
            saveDefaultConfig();
            this.formatsFile = new YAMLConfigurationFile(this, "formats.yml");
            this.formatsFile.saveDefaultConfig();
            onModuleEnable();
        } catch (Exception e) {
            e.printStackTrace();
            onFailureToEnable();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

    }

    /* Delegated Methods */
    protected void onModuleEnable() {}
    protected void onModuleDisable() {}
    protected void onFailureToEnable() {}
    protected void onFailureToDisable() {}

    /* Util methods */
    public <T extends Listener> T registerListener(T listener) {
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    public void logMessage(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    /* Formatting methods */

}
