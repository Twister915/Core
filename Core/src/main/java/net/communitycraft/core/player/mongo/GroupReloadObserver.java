package net.communitycraft.core.player.mongo;

interface GroupReloadObserver {
    void onReloadPermissions(CMongoPermissionsManager manager);
}
