package net.communitycraft.core.modular.command;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import net.communitycraft.core.modular.ModularPlugin;

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
        module.getCommand(command.getName()).setExecutor(command); //Get the command from the plugin.yml (NPE WARNING) and set it's executor
        this.topLevelCommands.put(command.getName(), command); //Put it in the hash map now that we've registered it.
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
