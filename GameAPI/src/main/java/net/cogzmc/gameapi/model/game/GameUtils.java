package net.cogzmc.gameapi.model.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public final class GameUtils {
    public static void hidePlayerFromPlayers(Player player, Iterable<Player> toHideFrom) {
        for (Player player1 : toHideFrom) {
            player1.hidePlayer(player);
        }
    }

    public static void hidePlayersFrom(Iterable<Player> playersToHide, Player toHideFrom) {
        for (Player player : playersToHide) {
            toHideFrom.hidePlayer(player);
        }
    }

    public static void showPlayerToPlayers(Player player, Iterable<Player> toShowTo) {
        for (Player player1 : toShowTo) {
            player1.showPlayer(player);
        }
    }

    public static void showPlayersToPlayer(Iterable<Player> playersToShow, Player player) {
        for (Player player1 : playersToShow) {
            player.showPlayer(player1);
        }
    }

    public static void hidePlayerFromAll(Player player) {
        hidePlayerFromPlayers(player, Arrays.asList(Bukkit.getOnlinePlayers()));
    }

    public static void showPlayerToAll(Player player) {
        showPlayerToPlayers(player, Arrays.asList(Bukkit.getOnlinePlayers()));
    }
}
