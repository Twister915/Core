package net.communitycraft.permissions;

import lombok.Getter;
import net.communitycraft.core.Core;
import net.communitycraft.core.model.ModelStorage;
import net.communitycraft.core.modular.ModularPlugin;
import net.communitycraft.core.modular.ModuleMeta;
import net.communitycraft.core.player.CGroup;
import net.communitycraft.core.player.CPermissionsManager;

@ModuleMeta(
        name = "Permissions Manager",
        description = "Provides commands for the permissions plugin, along with creating the default groups and assigning people groups on default."
)
public final class PermissionsManager extends ModularPlugin {
    @Getter private static PermissionsManager instance;
    @Getter private ModelStorage<PermissionChange> changeLog;

    @Override
    public void onModuleEnable() {
        instance = this;
        //Create default group
        CPermissionsManager permissionsManager = Core.getPermissionsManager();
        if (permissionsManager.getGroups().size() == 0) {
            CGroup defaultGroup = permissionsManager.createNewGroup("Default");
            defaultGroup.setPermission("test.permission", true);
        }
        //Get the changelog
        changeLog = Core.getModelManager().getModelStorage(PermissionChange.class);
        changeLog.reload();
    }
}
