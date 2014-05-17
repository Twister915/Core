package net.communitycraft.permissions;

import net.communitycraft.core.modular.ModularPlugin;
import net.communitycraft.core.modular.ModuleMeta;
import net.communitycraft.core.player.CGroup;
import net.communitycraft.core.player.CPermissionsManager;

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
