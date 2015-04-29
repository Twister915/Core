package net.cogzmc.core.modular;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.config.YAMLConfigurationFile;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.modular.command.ModuleCommandMap;
import net.cogzmc.core.network.NetworkManager;
import net.cogzmc.core.player.CPermissionsManager;
import net.cogzmc.core.player.CPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModularPlugin extends JavaPlugin {
    @Getter private ModuleMeta meta;
    @Getter private ModuleCommandMap commandMap;
    @Getter private Gson gson = getNewGson();
    @Getter private Formatter formatter;

    protected Gson getNewGson() {
        return getGsonBuilder().create();
    }

    protected GsonBuilder getGsonBuilder() {
        return new GsonBuilder();
    }

    @Override
    public final void onEnable() {
        try {
            if (!Core.getInstance().isEnabled()) {
                onFailureToEnable();
                return;
            }
            Core.getInstance().onModulePreEnable(this);
            if (getClass().isAnnotationPresent(UsesFormats.class)) {
                YAMLConfigurationFile formatsFile = new YAMLConfigurationFile(this, getClass().getAnnotation(UsesFormats.class).file());
                formatsFile.saveDefaultConfig();
                formatter = new Formatter(formatsFile);
            }
            meta = getClass().getAnnotation(ModuleMeta.class);
            if (meta == null) throw new IllegalStateException("You must annotate your class with the @" + ModuleMeta.class.getName() + " annotation!");
            saveDefaultConfig();
            this.commandMap = new ModuleCommandMap(this);
            onModuleEnable();
        } catch (Exception e) {
            e.printStackTrace();
            onFailureToEnable();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        logMessage("&cModule &6" + meta.name() + " &a&lEnabled");
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
        logMessage("&cModule &6" + meta.name() + " &4&lDisabled");
    }

    /* Delegated Methods */
    protected void onModuleEnable() throws Exception{getLogger().warning(getName() + " did not run any code on enable!");}
    protected void onModuleDisable() throws Exception{}
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

    @Deprecated
    public final String getFormatRaw(String key, String[]... formatters) {
        Formatter.FormatBuilder begin = formatter.begin(key);
        for (String[] strings : formatters) {
            if (strings.length != 2) continue;
            begin.withModifier(strings[0], strings[1]);
        }
        return begin.withPrefix(false).get();
    }

    @Deprecated
    public final String getFormat(String key, boolean prefix, String[]... formatters) {
        String formatRaw = getFormatRaw(key, formatters);
        String prefixString = getFormatRaw("prefix");
        return !prefix || prefixString == null ? formatRaw : prefixString + formatRaw;
    }

    @Deprecated
    public final String getFormat(String key, String[]... formatters) {
        return getFormat(key, true, formatters);
    }

    @Deprecated
    public final String getFormat(String key) {
        //noinspection NullArgumentToVariableArgMethod
        return getFormat(key, true, null);
    }

    @Deprecated
    public final boolean hasFormat(String key) {
        return formatter.has(key);
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
}
