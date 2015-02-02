package net.cogzmc.gameapi.model.game;

import net.cogzmc.gameapi.model.arena.Arena;

public class GameMessageDelegate<ArenaType extends Arena> extends GameDelegate<ArenaType>{
    protected GameMessageDelegate(Game<ArenaType> game) {
        super(game);
    }

    protected String getMoveMessage(CPlayer player, Point to, Point from) {return getGame().getAPIFormat("default.no-move");}
}
