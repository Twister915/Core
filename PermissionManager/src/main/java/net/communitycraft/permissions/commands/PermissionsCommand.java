package net.communitycraft.permissions.commands;

import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.communitycraft.permissions.commands.group.GroupSubCommand;
import net.communitycraft.permissions.commands.player.PlayerSubCommand;

@CommandMeta(
        aliases = {"perm", "perms"}
)
public final class PermissionsCommand extends ModuleCommand {
    /*
     * The command should be structured as follows
     *  - player
     *      - setgroup [name] [group] D
     *      - addgroup [name] [group] D
     *      - delgroup [name] [group] D
     *      - has [name] [permission] D
     *      - set [name] [permission] D
     *      - unset [name] [permission] D
     *      - delete [name]
     *      - show [name]
     *      - prefix [name] [prefix]
     *      - chatcolor [name] [color] D
     *      - tabcolor [name] [tabcolor]
     *  - group
     *      - set [name] [permission] D
     *      - has [name] [permission] D
     *      - unset [name] [permission] D
     *      - show [name]
     *      - prefix [name] [prefix]
     *      - chatcolor [name] [color] D
     *      - tabcolor [name] [tabcolor]
     *      - create [name] D
     *      - delete [name] D
     *      - list D
     *  - refresh
     *      - network
     */
    public PermissionsCommand() {
        super("permissions", new PlayerSubCommand(), new GroupSubCommand(), new RefreshSubCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
