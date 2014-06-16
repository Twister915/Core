package net.cogzmc.gameapi.model.game;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.GameAPI;
import net.cogzmc.gameapi.model.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class serves to represent a game that can be played. The rules of a game are modified through the delegated calls in the two
 */
@Data
@Setter(AccessLevel.NONE)
public class Game<ArenaType extends Arena> {
    private final ArenaType arena; //Arena this is being played in
    private final GameActionDelegate<ArenaType> actionDelegate; //Action delegate; we tell this once something happens
    private final GameRuleDelegate<ArenaType> ruleDelegate; //Rule delegate; we tell this before something happens
    private final ModularPlugin owner;
    private final GameMeta meta;
    private final String prefix;
    private final GameAPI gameAPI;
    private final GameListener<ArenaType> listener;
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

    private boolean loaded = false;
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
                GameRuleDelegate<ArenaType> ruleDelegate, GameMeta meta) {
        this.owner = owner;
        this.arena = arena;
        this.actionDelegate = actionDelegate;
        this.ruleDelegate = ruleDelegate;
        this.meta = meta;
        this.players = new HashSet<>(players);
        spectators = new HashSet<>();
        participants = new HashSet<>();
        gameAPI = Core.getInstance().getModuleProvider(GameAPI.class);
        assert gameAPI != null;
        prefix = getAPIFormat("prefix", false);
        for (CPlayer player : this.players) {
            this.participants.add(player.getOfflinePlayer());
        }
        listener = new GameListener<>(this);
        Bukkit.getPluginManager().registerEvents(listener, owner);
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

    public final boolean isPlaying(CPlayer player) {
        return players.contains(player);
    }

    public final boolean isSpectating(CPlayer player) {
        return spectators.contains(player);
    }

    public final boolean isInvolvedInGame(CPlayer player) {
        return spectators.contains(player) || players.contains(player);
    }

    /**
     *
     * @return
     */
    public final ImmutableSet<Player> getBukkitPlayers() {
        HashSet<Player> players1 = new HashSet<>();
        for (CPlayer player : players) {
            players1.add(player.getBukkitPlayer());
        }
        return ImmutableSet.copyOf(players1);
    }

    public void load() {
        if (loaded) throw new IllegalStateException("The game has already been loaded!");
        arena.load();
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

    void playerLeft(CPlayer player) {}
    void playerJoined(CPlayer player) {}

    private String formatUsingMeta(String original) {
        return original;
    }

    private String getModularFormat(ModularPlugin plugin, String key, Boolean prefix, String[]... formatters) {
        String format = plugin.getFormat(key, formatters);
        for (String[] formatter : formatters) {
            if (formatter.length != 2) continue;
            format = format.replaceAll(formatter[0], formatter[1]);
        }
        if (prefix) format = this.prefix + formatUsingMeta(format);
        return ChatColor.translateAlternateColorCodes('&', format);
    }

    /**
     * This is used to get a format that is shared by the GameAPI games.
     * @param key The formatter key
     * @param prefix The prefix
     * @param formatters The formatters
     * @return A formatted string from the key.
     */
    protected final String getAPIFormat(String key, Boolean prefix, String[]... formatters) {
        return getModularFormat(gameAPI, key, prefix, formatters);
    }

    protected final String getAPIFormat(String key, String[]... formatters) {
        return getAPIFormat(key, true, formatters);
    }

    protected final String getPluginFormat(String key, Boolean prefix, String[]... formatters) {
        return getModularFormat(owner, key, prefix, formatters);
    }

    protected final String getPluginFormat(String key, String[]... formatters) {
        return getPluginFormat(key, true, formatters);
    }

    public final void addSpectator(CPlayer player) {
        if (players.contains(player)) throw new IllegalArgumentException("Call makePlayerSpectator instead!");
        spectators.add(player);
        GameUtils.hidePlayerFromPlayers(player.getBukkitPlayer(), getBukkitPlayers());
        player.resetPlayer();
        player.addStatusEffect(PotionEffectType.INVISIBILITY, 2);
        player.giveItem(Material.BOOK, getAPIFormat(""));
    }

    public final void finishGame() {

    }
}
