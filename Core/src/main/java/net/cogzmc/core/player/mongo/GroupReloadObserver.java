package net.cogzmc.core.player.mongo;

public interface GroupReloadObserver {
    void onReloadPermissions(CMongoPermissionsManager manager);
}
