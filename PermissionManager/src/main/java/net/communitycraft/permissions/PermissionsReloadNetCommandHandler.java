package net.communitycraft.permissions;

import lombok.Data;
import net.communitycraft.core.Core;
import net.communitycraft.core.network.NetCommandHandler;
import net.communitycraft.core.network.NetworkServer;

@Data
final class PermissionsReloadNetCommandHandler implements NetCommandHandler<PermissionsReloadNetCommand> {
    private final String ourName;

    @Override
    public void handleNetCommand(NetworkServer sender, PermissionsReloadNetCommand netCommand) {
        if (sender.getName().equals(ourName)) return;
        Core.logInfo("Reloading permissions as per the request of " + sender.getName());
        Core.getPermissionsManager().reloadPermissions();
    }
}
