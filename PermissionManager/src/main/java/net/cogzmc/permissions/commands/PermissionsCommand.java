package net.cogzmc.permissions.commands;

import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.modular.command.CommandPermission;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.permissions.commands.group.GroupSubCommand;
import net.cogzmc.permissions.commands.player.PlayerSubCommand;

@CommandMeta(
        aliases = {"perm", "perms"}
)
@CommandPermission("permissions.manage")
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
     *      - delete [name] D
     *      - show [name] D
     *      - prefix [name] [prefix] D
     *      - suffix [name] [suffix] D
     *      - chatcolor [name] [color] D
     *      - tabcolor [name] [tabcolor] D
     *  - group
     *      - set [name] [permission] D
     *      - has [name] [permission] D
     *      - unset [name] [permission] D
     *      - show [name] D
     *      - prefix [name] [prefix] D
     *      - suffix [name] [suffix] D
     *      - chatcolor [name] [color] D
     *      - tabcolor [name] [tabcolor] D
     *      - create [name] D
     *      - delete [name] D
     *      - list D
     *  - refresh
     *      - network D
     */
    public PermissionsCommand() {
        super("permissions", new PlayerSubCommand(), new GroupSubCommand(), new RefreshSubCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
