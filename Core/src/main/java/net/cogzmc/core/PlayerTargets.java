package net.cogzmc.core;

import net.cogzmc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public interface PlayerTargets extends Iterable<CPlayer> {
    static PlayerTargets getForAllPlayers() {
        //have to cast this because it returns Iterator<? extends Player> when we need Iterator<Player>
        //noinspection unchecked
        return () -> (Iterator<CPlayer>) Bukkit.getOnlinePlayers().stream().map(Core::getOnlinePlayer).iterator();
    }

    static PlayerTargets getForCPlayers(Collection<CPlayer> players) {
        return players::iterator;
    }

    static PlayerTargets getForPlayers(CPlayer... player) {
        return () -> Arrays.asList(player).iterator();
    }

    static PlayerTargets getForPlayers(Collection<Player> players) {
        return () -> players.stream().map(Core::getOnlinePlayer).iterator();
    }

    static PlayerTargets getForPlayers(Player... player) {
        return  () -> Arrays.stream(player).map(Core::getOnlinePlayer).iterator();
    }
}
