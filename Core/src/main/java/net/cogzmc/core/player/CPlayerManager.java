package net.cogzmc.core.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The CPlayerManager can be used to access information about players or iterate through all online players in an enhanced for loop
 *
 * <pre>
 *     for ({@link net.cogzmc.core.player.CPlayer} player : playerManagerInstance) {
 *
 *     }
 * </pre>
 *
 * the code above will work and allow you to iterate through all online players.
 *
 * You can also get offline players by username, uuid, or a list of UUIDs.
 *
 * You can get any online player by name, UUID, or {@link org.bukkit.entity.Player} representation.
 *
 * You should never call {@link #playerLoggedIn(org.bukkit.entity.Player, java.net.InetAddress)} or {@link #playerLoggedOut(org.bukkit.entity.Player)} unless you are a listener for the {@link net.cogzmc.core.player.CPlayerManager}.
 * It is unsafe to call these methods unless you are aware of the effect as it can break the server easily.
 *
 * @author Joey
 */
public interface CPlayerManager extends Iterable<CPlayer>, CPlayerRepository {
    /**
     * Gets an {@link net.cogzmc.core.player.COfflinePlayer} for an {@link org.bukkit.OfflinePlayer} object provided by Bukkit.
     * @param player The {@link org.bukkit.OfflinePlayer} object that you wish to get a {@link net.cogzmc.core.player.COfflinePlayer} for.
     * @return A {@link net.cogzmc.core.player.COfflinePlayer} that represents the {@link org.bukkit.OfflinePlayer} you passed above.
     */
    COfflinePlayer getCOfflinePlayerForOfflinePlayer(OfflinePlayer player);
    /**
     * Gets all currently online players in a collection.
     * @return Online {@link net.cogzmc.core.player.CPlayer}s that are currently online.
     */
    Collection<CPlayer> getOnlinePlayers();

    /**
     * Gets the {@link net.cogzmc.core.player.CPlayer} object that represents the {@link org.bukkit.entity.Player} object from Bukkit.
     * @param player The {@link org.bukkit.entity.Player} from Bukkit to find the {@link net.cogzmc.core.player.CPlayer} for
     * @return {@link net.cogzmc.core.player.CPlayer} instance for this {@link org.bukkit.entity.Player}
     */
    CPlayer getCPlayerForPlayer(Player player);

    /**
     * Gets a {@link net.cogzmc.core.player.CPlayer} that is identified by the unique identifier specified in the parameters.
     * @param uuid The {@link java.util.UUID} representing the UUID of the player.
     * @return A {@link net.cogzmc.core.player.CPlayer} object that represents any matching player, or {@code null} if the player was not found to be online.
     */
    CPlayer getOnlineCPlayerForUUID(UUID uuid);

    /**
     * Gets an online player by the username specified.
     * @param name The name of the player that you are looking for.
     * @return A {@link net.cogzmc.core.player.CPlayer} instance if the player is found or {@code null} if there is no matching player.
     */
    CPlayer getOnlineCPlayerForName(String name);

    /**
     * This will allow you to get a {@link java.util.List} of {@link net.cogzmc.core.player.CPlayer}s by simply sending the start of a person's name.
     * @param name The name of the player that you wish to autocomplete.
     * @return A {@link java.util.List} of {@link net.cogzmc.core.player.CPlayer} objects for this name.
     */
    List<CPlayer> getCPlayerByStartOfName(String name);

    /**
     * Called strictly by a listener, internal method that you should <b>NEVER</b> call under normal circumstances.
     * @param player The {@link org.bukkit.entity.Player} object that represents the player who is logging in.
     * @param address The {@code player}'s {@link java.net.InetAddress} representing their connection address for logging.
     */
    void playerLoggedIn(Player player, InetAddress address) throws CPlayerJoinException;


    /**
     * Called strictly by a listener, internal method that you should <b>NEVER</b> call under normal circumstances.
     * @param player The {@link org.bukkit.entity.Player} object that represents the player who is logging out.
     */
    void playerLoggedOut(Player player);

    /**
     * <b>Should only be called during disable, internal method!</b>
     */
    void onDisable();

    /**
     *
     * @param processor
     */
    void registerCPlayerConnectionListener(CPlayerConnectionListener processor);

    /**
     *
     * @param processor
     */
    void unregisterCPlayerConnectionListener(CPlayerConnectionListener processor);

    GeoIPManager setupNewGeoIPManager(File dbFile) throws IOException;
    GeoIPManager getGeoIPManager();
}
