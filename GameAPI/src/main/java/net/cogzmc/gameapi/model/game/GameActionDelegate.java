package net.cogzmc.gameapi.model.game;

import net.cogzmc.gameapi.model.arena.Arena;

public abstract class GameActionDelegate<ArenaType extends Arena> extends GameDelegate<ArenaType> {
    protected GameActionDelegate(Game<ArenaType> game) {
        super(game);
    }
}
