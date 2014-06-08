package net.cogzmc.permissions;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.network.NetCommandHandler;
import net.cogzmc.core.network.NetworkServer;

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
