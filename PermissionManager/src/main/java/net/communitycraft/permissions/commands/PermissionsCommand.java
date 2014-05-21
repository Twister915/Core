package net.communitycraft.permissions.commands;

import net.communitycraft.core.modular.command.ModuleCommand;
import net.communitycraft.core.player.CPlayer;

public final class PermissionsCommand extends ModuleCommand {
    /*
     * The command should be structured as follows
     *  - player
     *      - setgroup [name] [group]
     *      - addgroup [name] [group]
     *      - delgroup [name] [group]
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
        super("permissions");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) {

    }
}
