package net.communitycraft.core.modular.command;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import net.communitycraft.core.modular.ModularPlugin;

import java.util.HashMap;
import java.util.Map;

@Data
public final class ModuleCommandMap {
    @Getter(AccessLevel.NONE) private final Map<String, ModuleCommand> topLevelCommands = new HashMap<>();
    private final ModularPlugin module;

    public void registerCommand(ModuleCommand command) {
        if (topLevelCommands.containsKey(command.getName())) return;
        module.getCommand(command.getName()).setExecutor(command);
        this.topLevelCommands.put(command.getName(), command);
    }

    public ModuleCommand getCommandByName(String name) {
        return topLevelCommands.get(name);
    }
}
