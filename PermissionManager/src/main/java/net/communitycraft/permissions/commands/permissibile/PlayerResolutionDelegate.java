package net.communitycraft.permissions.commands.permissibile;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;

import java.util.List;

public final class PlayerResolutionDelegate implements PermissibleResolutionDelegate<COfflinePlayer> {
    @Override
    public COfflinePlayer getFor(String name) {
        List<COfflinePlayer> offlinePlayerByName = Core.getPlayerManager().getOfflinePlayerByName(name);
        if (offlinePlayerByName.size() >= 1) return offlinePlayerByName.get(0);
        return null;
    }
}
