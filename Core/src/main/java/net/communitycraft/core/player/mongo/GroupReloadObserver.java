package net.communitycraft.core.player.mongo;

public interface GroupReloadObserver {
    void onReloadPermissions(CMongoPermissionsManager manager);
}
