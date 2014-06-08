package net.cogzmc.gameapi.model.game;

import net.cogzmc.gameapi.model.arena.Arena;

public abstract class GameRuleDelegate<ArenaType extends Arena> extends GameDelegate<ArenaType> {
    protected GameRuleDelegate(Game<ArenaType> game) {
        super(game);
    }
}
