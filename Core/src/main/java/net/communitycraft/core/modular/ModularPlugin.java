package net.communitycraft.core.modular;

import lombok.Getter;
import net.communitycraft.core.Core;
import net.communitycraft.core.config.YAMLConfigurationFile;
import net.communitycraft.core.modular.command.ModuleCommand;
import net.communitycraft.core.modular.command.ModuleCommandMap;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModularPlugin extends JavaPlugin {

    private YAMLConfigurationFile formatsFile;
    @Getter private ModuleMeta meta;
    @Getter private ModuleCommandMap commandMap;

    @Override
    public void onEnable() {
        try {
            if (!Core.getInstance().isEnabled()) onFailureToEnable();
            meta = getClass().getAnnotation(ModuleMeta.class);
            saveDefaultConfig();
            this.formatsFile = new YAMLConfigurationFile(this, "formats.yml");
            this.formatsFile.saveDefaultConfig();
            onModuleEnable();
        } catch (Exception e) {
            e.printStackTrace();
            onFailureToEnable();
            getServer().getPluginManager().disablePlugin(this);
        }
        logMessage("&cModule " + meta.name() + " &a&lEnabled");
    }

    @Override
    public void onDisable() {
        try {
            onModuleDisable();
        } catch (Exception e) {
            onFailureToDisable();
            e.printStackTrace();
        }
        logMessage("&cModule " + meta.name() + " &4&lDisabled");
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

    protected void addCommand(ModuleCommand command) {
        this.commandMap.registerCommand(command);
    }

    /* Formatting methods */


}
