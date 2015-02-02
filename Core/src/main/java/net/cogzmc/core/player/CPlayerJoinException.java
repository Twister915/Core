package net.cogzmc.core.player;

@EqualsAndHashCode(callSuper = false)
@Data
/**
 * Throw this when you wish to stop a player from joining the server in the {@link net.cogzmc.core.player.CPlayerConnectionListener#onPlayerLogin(CPlayer, java.net.InetAddress)} method
 *
 * @author Joey
 * @see net.cogzmc.core.player.CPlayerConnectionListener
 * @see net.cogzmc.core.player.CPlayerManager
 * @since 1.0
 */
public class CPlayerJoinException extends Exception {
    private final String disconectMessage;
}
