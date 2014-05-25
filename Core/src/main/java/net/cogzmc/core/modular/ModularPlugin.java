package net.cogzmc.core.modular;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.config.YAMLConfigurationFile;
import net.cogzmc.core.model.ModelManager;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.modular.command.ModuleCommandMap;
import net.cogzmc.core.netfiles.NetFileManager;
import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.player.CPermissionsManager;
import net.cogzmc.core.player.CPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModularPlugin extends JavaPlugin {
    private YAMLConfigurationFile formatsFile;
    @Getter private ModuleMeta meta;
    @Getter private ModuleCommandMap commandMap;

    @Override
    public final void onEnable() {
        try {
            if (!Core.getInstance().isEnabled()) {
                onFailureToEnable();
                return;
            }
            Core.getInstance().onModulePreEnable(this);
            meta = getClass().getAnnotation(ModuleMeta.class);
            saveDefaultConfig();
            this.formatsFile = new YAMLConfigurationFile(this, "formats.yml");
            this.formatsFile.saveDefaultConfig();
            this.commandMap = new ModuleCommandMap(this)    ;
            onModuleEnable();
        } catch (Exception e) {
            e.printStackTrace();
            onFailureToEnable();
            getServer().getPluginManager().disablePlugin(this);
        }
        logMessage("&cModule " + meta.name() + " &a&lEnabled");
    }

    @Override
    public final void onDisable() {
        try {
            onModuleDisable();
            Core.getInstance().onModulePreDisable(this);
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
    public final <T extends Listener> T registerListener(T listener) {
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    public final <T extends ModuleCommand> T registerCommand(T command) {
        getCommandMap().registerCommand(command);
        return command;
    }

    public final void logMessage(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6" + getMeta().name() + "&7] " + message));
    }

    protected final void addCommand(ModuleCommand command) {
        this.commandMap.registerCommand(command);
    }

    protected final ModuleCommand getModuleCommand(String name) {
        return this.commandMap.getCommandByName(name);
    }

    /* Formatting methods */

    public final String getFormatRaw(String key, String[]... formatters) {
        FileConfiguration config = formatsFile.getConfig(); //Get the formats file
        if (!config.contains(key)) return null; //Check if it has this format key, and if not return null
        String unFormattedString = ChatColor.translateAlternateColorCodes('&',config.getString(key)); //Get the un-formatted key
        for (String[] formatter : formatters) { //Iterate through the formatters
            if (formatter.length < 2) continue; //Validate the length
            unFormattedString = unFormattedString.replace(formatter[0], formatter[1]); //Replace all in the unformatted string
        }
        return unFormattedString; //Return
    }

    public final String getFormat(String key, boolean prefix, String[]... formatters) {
        String formatRaw = getFormatRaw(key, formatters);
        String prefix1 = getFormatRaw("prefix");
        return !prefix || prefix1 == null ? formatRaw : prefix1 + formatRaw;
    }

    public final String getFormat(String key, String[]... formatters) {
        return getFormat(key, true, formatters);
    }

    public final String getFormat(String key) {
        return getFormatRaw(key);
    }

    public final boolean hasFormat(String key) {
        return formatsFile.getConfig().contains(key);
    }

    public final CPlayerManager getPlayerManager() {
        return Core.getPlayerManager();
    }

    public final NetworkManager getNetworkManager() {
        return Core.getNetworkManager();
    }

    public final CPermissionsManager getPermissionsManager() {
        return Core.getPermissionsManager();
    }

    public final ModelManager getModelManager() {
        return Core.getModelManager();
    }

    public final NetFileManager getNetFileManager() {
        return Core.getNetFileManager();
    }
}
