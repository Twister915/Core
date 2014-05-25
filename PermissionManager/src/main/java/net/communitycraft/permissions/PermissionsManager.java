package net.communitycraft.permissions;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.model.ModelStorage;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.CPermissionsManager;
import net.communitycraft.permissions.commands.PermissionsCommand;

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
            permissionsManager.saveGroup(defaultGroup);
        }
        registerCommand(new PermissionsCommand());
        //Get the changelog
        changeLog = Core.getModelManager().getModelStorage(PermissionChange.class);
        changeLog.reload();
    }
}
