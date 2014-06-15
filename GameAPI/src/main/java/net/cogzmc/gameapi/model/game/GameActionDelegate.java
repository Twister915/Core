package net.cogzmc.gameapi.model.game;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.gameapi.model.arena.Point;

/**
 * The action delegate will be told by a game <b>after</b> something happens so that the game logic can be executed
 * based on the action that has occurred.
 *
 * @param <ArenaType> The type of {@link net.cogzmc.gameapi.model.arena.Arena} that the game
 */
public abstract class GameActionDelegate<ArenaType extends Arena> extends GameDelegate<ArenaType> {
    protected GameActionDelegate(Game<ArenaType> game) {
        super(game);
    }

    protected void onGameStart() {}
    protected void onPlayerJoinGame(CPlayer player) {}
    protected void onPlayerLeaveGame(CPlayer player) {}
    protected void onSpectatorJoinGame(CPlayer player) {}
    protected void onPlayerBecomeSpectator(CPlayer player) {}
    protected void onPlayerMove(CPlayer player, Point from, Point to) {}
}
