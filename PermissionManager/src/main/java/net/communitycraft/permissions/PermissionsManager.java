package net.communitycraft.permissions;

import me.twister915.core.modular.ModularPlugin;
import me.twister915.core.modular.ModuleMeta;
import me.twister915.core.player.CGroup;
import me.twister915.core.player.CPermissionsManager;

@ModuleMeta(
        name = "Permissions Manager",
        description = "Provides commands for the permissions plugin, along with creating the default groups and assigning people groups on default."
)
public final class PermissionsManager extends ModularPlugin {
    @Override
    public void onModuleEnable() {
        //Create default group
        CPermissionsManager permissionsManager = getPlayerManager().getPermissionsManager();
        if (permissionsManager.getGroups().size() == 0) {
            CGroup aDefault = permissionsManager.createNewGroup("Default");
            aDefault.setPermission("test.permission", true);
        }
    }
}
