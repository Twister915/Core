package net.cogzmc.core.modular.command;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
/**
 * This is a command map for the module, and will most likely be modified to provide some level of more interesting functionality.
 */
public final class ModuleCommandMap {
    //Getter implied due to the @Data annotation on this class.
    //Getter forbidden so that it's not modified except for what is exposed via the methods below
    @Getter(AccessLevel.NONE) private final Map<String, ModuleCommand> topLevelCommands = new HashMap<>();
    private final ModularPlugin module;

    /**
     * Registers a command for handling.
     * @param command The command to register.
     */
    public void registerCommand(ModuleCommand command) {
        //Check if we have the command registered using the same name
        if (topLevelCommands.containsKey(command.getName())) return; //Return if so
        PluginCommand command1 = getCommand(command.getName(), module); //Create a command for force registration
        command1.setExecutor(command); //Set the exectuor
        command1.setTabCompleter(command); //Tab completer
        CommandMeta annotation = command.getClass().getAnnotation(CommandMeta.class); //Get the commandMeta
        if (annotation != null){
            command1.setAliases(Arrays.asList(annotation.aliases()));
            command1.setDescription(annotation.description());
            command1.setUsage(annotation.usage());
        }
        getCommandMap().register(module.getDescription().getName(), command1); //Register it with Bukkit
        this.topLevelCommands.put(command.getName(), command); //Put it in the hash map now that we've registered it.
    }

    /**
     * Creates a new instance of the command
     *
     * @return new PluginCommand instance of the requested command name
     */
    private PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor commandConstructor = PluginCommand.class.getDeclaredConstructor(new Class[]{String.class, Plugin.class});
            commandConstructor.setAccessible(true);
            command = (PluginCommand) commandConstructor.newInstance(name, plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return command;
    }

    /**
     * Gets the command map from bukkit
     *
     * @return The command map from bukkit
     */
    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            PluginManager pluginManager = Bukkit.getPluginManager();
            Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(pluginManager);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return commandMap;
    }

    /**
     * Gets a current command by the name you specify.
     * @param name The name you are looking for.
     * @return The command by that name or null if it cannot find the command.
     */
    public ModuleCommand getCommandByName(String name) {
        return topLevelCommands.get(name);
    }
}
