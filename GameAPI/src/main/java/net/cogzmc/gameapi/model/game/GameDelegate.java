package net.cogzmc.gameapi.model.game;

import lombok.Getter;
import net.cogzmc.gameapi.model.arena.Arena;

public abstract class GameDelegate<ArenaType extends Arena> {
    @Getter private final Game<ArenaType> game;

    protected GameDelegate(Game<ArenaType> game) {
        this.game = game;
    }

    protected ArenaType getArena() {
        return game.getArena();
    }
}
