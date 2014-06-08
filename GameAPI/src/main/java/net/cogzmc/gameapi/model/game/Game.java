package net.cogzmc.gameapi.model.game;

import lombok.Data;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import org.bukkit.event.Listener;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
/**
 * This class serves to represent a game that can be played. The rules of a game are modified through the delegated calls in the two
 */
public abstract class Game<ArenaType extends Arena> implements Listener {
    private final ArenaType arena; //Arena this is being played in
    private final GameActionDelegate<ArenaType> actionDelegate; //Action delegate; we tell this once something happens
    private final GameRuleDelegate<ArenaType> ruleDelegate; //Rule delegate; we tell this before something happens
    private final ModularPlugin owner;
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

    private boolean running;
    private Date timeStarted;

    public Game(ModularPlugin owner, ArenaType arena, Set<CPlayer> players, GameActionDelegate<ArenaType> actionDelegate,
                GameRuleDelegate<ArenaType> ruleDelegate) {
        this.owner = owner;
        this.arena = arena;
        this.actionDelegate = actionDelegate;
        this.ruleDelegate = ruleDelegate;
        this.players = new HashSet<>(players);
        this.spectators = new HashSet<>();
        this.participants = new HashSet<>();
        for (CPlayer player : this.players) {
            this.participants.add(player.getOfflinePlayer());
        }
    }

    public void start() {

    }
}
