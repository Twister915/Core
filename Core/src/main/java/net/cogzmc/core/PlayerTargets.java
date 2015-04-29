package net.cogzmc.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;

public interface PlayerTargets extends Iterable<Player> {
    static PlayerTargets getForAllPlayers() {
        //have to cast this because it returns Iterator<? extends Player> when we need Iterator<Player>
        //noinspection unchecked
        return () -> ((Iterator<Player>) Bukkit.getOnlinePlayers().iterator());
    }

    static PlayerTargets getForPlayers(Iterator<Player> players) {
        return () -> players;
    }

    static PlayerTargets getForPlayers(Player... player) {
        return () -> Arrays.asList(player).iterator();
    }
}
