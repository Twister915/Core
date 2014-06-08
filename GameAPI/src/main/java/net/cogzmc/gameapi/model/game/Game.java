package net.cogzmc.gameapi.model.game;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.GameAPI;
import net.cogzmc.gameapi.model.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Value
/**
 * This class serves to represent a game that can be played. The rules of a game are modified through the delegated calls in the two
 */
public class Game<ArenaType extends Arena> implements Listener {
    private final ArenaType arena; //Arena this is being played in
    private final GameActionDelegate<ArenaType> actionDelegate; //Action delegate; we tell this once something happens
    private final GameRuleDelegate<ArenaType> ruleDelegate; //Rule delegate; we tell this before something happens
    private final ModularPlugin owner;
    private final String prefix;
    private final GameAPI gameAPI;
    /**
     * For the initial players of the game (meaning, anyone who started the game playing) or anyone that joined the game to hold a "player" status.
     *
     * Spectators that join the game without ever first being a "player" are not included in this set.
     */
    @Getter(AccessLevel.NONE) private final Set<COfflinePlayer> participants;
    /**
     * Current players of the game.
     */
    @Getter(AccessLevel.NONE) private final Set<CPlayer> players;
    /**
     * Current spectators of the game.
     */
    @Getter(AccessLevel.NONE) private final Set<CPlayer> spectators;
    /**
     * Holds the current running countdowns.
     */
     @Getter(AccessLevel.NONE) private final Set<GameCountdown> runningCountdowns = new HashSet<>();

    private boolean running;
    private Date timeStarted;

    /**
     * Creates a new Game with all of the passed options.
     * @param owner The plugin who is managing this game.
     * @param arena The arena in which you wish to play the game.
     * @param players The players who will be initial participants in the game.
     * @param actionDelegate The action delegate for this game.
     * @param ruleDelegate The rule delegate for this game.
     */
    public Game(ModularPlugin owner, ArenaType arena, Set<CPlayer> players, GameActionDelegate<ArenaType> actionDelegate,
                GameRuleDelegate<ArenaType> ruleDelegate) {
        this.owner = owner;
        this.arena = arena;
        this.actionDelegate = actionDelegate;
        this.ruleDelegate = ruleDelegate;
        this.players = new HashSet<>(players);
        spectators = new HashSet<>();
        participants = new HashSet<>();
        gameAPI = Core.getInstance().getModuleProvider(GameAPI.class);
        assert gameAPI != null;
        prefix = getInternalFormat("prefix", false);
        for (CPlayer player : this.players) {
            this.participants.add(player.getOfflinePlayer());
        }
    }

    /**
     * Gets the players who are actively participating in the game.
     * @return A {@link com.google.common.collect.ImmutableSet} of {@link net.cogzmc.core.player.CPlayer} instances.
     */
    public final ImmutableSet<CPlayer> getPlayers() {
        return ImmutableSet.copyOf(players);
    }

    /**
     * Gets the active spectators of the game.
     * @return A {@link com.google.common.collect.ImmutableSet} of {@link net.cogzmc.core.player.CPlayer} instances.
     */
    public final ImmutableSet<CPlayer> getSpectators() {
        return ImmutableSet.copyOf(spectators);
    }

    /**
     * Gets players who were once members of the {@code players} {@link java.util.Set}
     * @return A {@link com.google.common.collect.ImmutableSet} of {@link net.cogzmc.core.player.COfflinePlayer} instances.
     */
    public final ImmutableSet<COfflinePlayer> getParticipants() {
        return ImmutableSet.copyOf(participants);
    }

    /**
     * Call this to start the game.
     */
    public void start() {
        this.timeStarted = new Date();
        this.running = true;
    }

    final void gameCountdownStarted(GameCountdown countdown) {
        runningCountdowns.remove(countdown);
    }

    final void gameCountdownEnded(GameCountdown countdown) {
        runningCountdowns.remove(countdown);
    }

    /**
     * This is used to get a format that is shared by the GameAPI games.
     * @param key The formatter key
     * @param prefix The prefix
     * @param formatters The formatters
     * @return A formatted string from the key.
     */
    protected final String getInternalFormat(String key, Boolean prefix, String[]... formatters) {
        String format = gameAPI.getFormat(key, formatters);
        for (String[] formatter : formatters) {
            if (formatter.length != 2) continue;
            format = format.replaceAll(formatter[0], formatter[1]);
        }
        return ChatColor.translateAlternateColorCodes('&', format);
    }
}
