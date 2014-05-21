package net.communitycraft.permissions.commands.general;

import net.communitycraft.core.Core;
import net.communitycraft.core.player.COfflinePlayer;

import java.util.List;

public abstract class AbstractPlayerSubCommand extends PermissibleSubCommand<COfflinePlayer> {
    protected AbstractPlayerSubCommand(String name) {
        super(name);
    }

    @Override
    protected COfflinePlayer getPermissible(String name) {
        List<COfflinePlayer> offlinePlayerByName = Core.getPlayerManager().getOfflinePlayerByName(name);
        if (offlinePlayerByName.size() >= 1) return offlinePlayerByName.get(0);
        return null;
    }

    @Override
    protected String getNameOfPermissibleType() {
        return "Player";
    }
}
