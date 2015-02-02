package net.cogzmc.permissions;

final class PermissionsReloadNetCommandHandler implements NetCommandHandler<PermissionsReloadNetCommand> {
    @Override
    public void handleNetCommand(NetworkServer sender, PermissionsReloadNetCommand netCommand) {
        if (sender.equals(Core.getNetworkManager().getThisServer())) return;
        Core.logInfo("Reloading permissions as per the request of " + sender.getName());
        Core.getPermissionsManager().reloadPermissions();
    }
}
