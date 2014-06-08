package net.cogzmc.gameapi.model.game;

import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.gameapi.model.team.TeamContext;

import java.util.Set;

public final class TeamGame<ArenaType extends Arena> extends Game<ArenaType> {
    private final TeamContext teamContext;
    public TeamGame(ModularPlugin owner, ArenaType arena, Set<CPlayer> players,
                    GameActionDelegate<ArenaType> actionDelegate, GameRuleDelegate<ArenaType> ruleDelegate,
                    TeamContext teamContext) {
        super(owner, arena, players, actionDelegate, ruleDelegate);
        this.teamContext = teamContext;
    }
}
