package net.cogzmc.gameapi.model.game;

import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.gameapi.model.team.Team;
import net.cogzmc.gameapi.model.team.TeamContext;

import java.util.Set;

public final class TeamGame<ArenaType extends Arena, TeamType extends Team> extends Game<ArenaType> {
    @Getter private final TeamContext<TeamType> teamContext;

    public TeamGame(ModularPlugin owner, ArenaType arena, Set<CPlayer> players,
                    GameActionDelegate<ArenaType> actionDelegate, GameRuleDelegate<ArenaType> ruleDelegate,
                    TeamContext<TeamType> teamContext, GameMeta meta) {
        super(owner, arena, players, actionDelegate, ruleDelegate, meta);
        this.teamContext = teamContext;
    }

    @Override
    void playerLeft(CPlayer player) {
        teamContext.playerLeftContext(player);
    }
}
