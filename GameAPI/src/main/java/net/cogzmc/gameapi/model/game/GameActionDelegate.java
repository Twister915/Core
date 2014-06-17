package net.cogzmc.gameapi.model.game;

import net.cogzmc.gameapi.model.arena.Arena;

/**
 * The action delegate will be told by a game <b>after</b> something happens so that the game logic can be executed
 * based on the action that has occurred.
 *
 * @param <ArenaType> The type of {@link net.cogzmc.gameapi.model.arena.Arena} that the game
 */
public abstract class GameActionDelegate<ArenaType extends Arena> extends GameDelegate<ArenaType> implements GameObserver {
    protected GameActionDelegate(Game<ArenaType> game) {
        super(game);
    }
}
