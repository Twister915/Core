package net.communitycraft.permissions.commands;

import net.communitycraft.core.modular.command.ModuleCommand;
import net.communitycraft.permissions.commands.player.PlayerSubCommand;

public final class PermissionsCommand extends ModuleCommand {
    /*
     * The command should be structured as follows
     *  - player
     *      - setgroup [name] [group] D
     *      - addgroup [name] [group] D
     *      - delgroup [name] [group] D
     *      - has [name] [permission]
     *      - set [name] [permission]
     *      - unset [name] [permission]
     *      - purge [name]
     *      - show [name]
     *      - prefix [name] [prefix]
     *      - chatcolor [name] [color]
     *      - tabcolor [name] [tabcolor]
     *  - group
     *      - set [name] [permission]
     *      - has [name] [permission]
     *      - unset [name] [permission]
     *      - show [name]
     *      - prefix [name] [prefix]
     *      - chatcolor [name] [color]
     *      - tabcolor [name] [tabcolor]
     *      - create [name]
     *      - purge [name]
     *      - list
     *  - refresh
     *      - network
     */
    public PermissionsCommand() {
        super("permissions", new PlayerSubCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
