package net.communitycraft.permissions.commands.permissibile;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;

import java.util.ArrayList;
import java.util.List;

public final class PlayerResolutionDelegate implements PermissibleResolutionDelegate<COfflinePlayer> {
    @Override
    public COfflinePlayer getFor(String name) {
        List<COfflinePlayer> offlinePlayerByName = Core.getPlayerManager().getOfflinePlayerByName(name);
        if (offlinePlayerByName.size() >= 1) return offlinePlayerByName.get(0);
        return null;
    }

    @Override
    public String getNameOfType() {
        return "Player";
    }

    @Override
    public List<String> getAutoCompleteFor(String s) {
        List<String> players = new ArrayList<>();
        for (CPlayer cPlayer : Core.getPlayerManager()) {
            String name = cPlayer.getName();
            if (name.startsWith(s)) players.add(name);
        }
        return players;
    }
}
