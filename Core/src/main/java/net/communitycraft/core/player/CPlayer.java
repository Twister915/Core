package net.communitycraft.core.player;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.net.InetAddress;

/**
 * A class to represent a player on the server.
 */
public interface CPlayer extends COfflinePlayer {
    /**
     * Gets the name of the player on the server.
     * @return A {@link java.lang.String} representing the player's name.
     */
    String getName();

    /**
     * Gets their current IP Address
     * @return The {@link java.net.InetAddress} representing their IP Address.
     */
    InetAddress getAddress();

    /**
     * Gets the status of the player (online or offline)
     * @return A boolean representing the player's online state.
     */
    boolean isOnline();

    /**
     * Gets if the player is joining our server for the first time.
     * @return A boolean denoting if the player is joining the server for the first time.
     */
    boolean isFirstJoin();

    /**
     * Sends any number of messages to the player.
     * @param messages The messages you wish to be sent to the player.
     */
    void sendMessage(String... messages);

    /**
     * Sends a message that is balanced vertically in the center as best as possible (upward aligned for odd numbered line counts) and fills the entire 10 line chat window.
     * @param messageLines The lines of text you want to send to the user.
     */
    void sendFullChatMessage(String... messageLines);

    /**
     * Clears the entire chat (50 lines) for the player by sending blank messages.
     */
    void clearChatAll();

    /**
     * Clears the visible chat (20 lines) for the player by sending blank messages.
     */
    void clearChatVisible();

    /**
     * Plays a sound for the player.
     * @param s The sound to play.
     * @param volume How loud to play it (distance heard).
     * @param pitch The pitch to play it at (speed).
     */
    void playSoundForPlayer(Sound s, Float volume, Float pitch);

    /**
     * Plays a sound for the player
     * @param s The sound to play.
     * @param volume How loud to play it (distance heard).
     */
    void playSoundForPlayer(Sound s, Float volume);

    /**
     * Plays a sound for the player
     * @param s The sound to play.
     */
    void playSoundForPlayer(Sound s);

    /**
     * Gives you the representation of the player from Bukkit using the {@link org.bukkit.entity.Player} object.
     * @return The player from Bukkit.
     */
    Player getBukkitPlayer();
}
