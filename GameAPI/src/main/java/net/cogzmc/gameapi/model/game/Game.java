package net.cogzmc.gameapi.model.game;

import lombok.Data;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.gameapi.model.team.TeamContext;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

@Data
/**
 * This class serves to represent a game that can be played. The rules of a game are modified through the delegated calls in the two
 */
public final class Game<ArenaType extends Arena> implements Listener {
    private final ArenaType arena;
    private final TeamContext teamContext;
    private final GameActionDelegate<ArenaType> actionDelegate;
    private final GameRuleDelegate<ArenaType> ruleDelegate;

    /**
     * For the initial players of the game (meaning, anyone who started the game playing) or anyone that joined the game to hold a "player" status.
     *
     * Spectators that join the game without ever first being a "player" are not included in this set.
     */
    private final Set<COfflinePlayer> participants;
    /**
     * Current players of the game.
     */
    private final Set<CPlayer> players;
    /**
     * Current spectators of the game.
     */
    private final Set<CPlayer> spectators;

    public Game(ArenaType arena, Set<CPlayer> players, TeamContext teamContext, GameActionDelegate<ArenaType> actionDelegate, GameRuleDelegate<ArenaType> ruleDelegate) {
        this.arena = arena;
        this.teamContext = teamContext;
        this.actionDelegate = actionDelegate;
        this.ruleDelegate = ruleDelegate;
        this.players = new HashSet<>(players);
        this.spectators = new HashSet<>();
        this.participants = new HashSet<>();
        this.participants.addAll(this.players);
    }

    public void start() {

    }
}
