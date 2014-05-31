package net.cogzmc.entityapi.sigmove;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.entityapi.EntityAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 28/05/2014
 */
public class CPlayerSignificantMoveManager implements Listener, CPlayerConnectionListener {

	// A List Of Listeners, listening into CPlayerSignificantMoveEvent
	private static volatile List<CPlayerSignificantMoveListener> listenerList = new ArrayList<>();

	public CPlayerSignificantMoveManager() {
		Core.getPlayerManager().registerCPlayerConnectionListener(this);
	}

	/**
	 * Register a move listener
	 * @param moveListener The move listener
	 */
	@SneakyThrows
	public static synchronized void registerListener(@NonNull final CPlayerSignificantMoveListener moveListener) {
		if(listenerList.contains(moveListener)) throw new SignificantMoveException("A CPlayerSignificantMoveListener was registered Twice!");

		moveListener.setBukkitTaskId(
			new BukkitRunnable() {

				@Override
				public void run() {

					Player[] players;

					Location centre = moveListener.getLocation();
					World world = centre.getWorld();

					Map<CPlayer, Location> lastSignificantLocation = moveListener.getLastSignificantLocation();
					Double squaredRadiusFromLocation = moveListener.getSquaredRadiusFromLocation();
					Player[] presetPlayers = moveListener.getPlayers();

					// If players has not been specified then
					if(presetPlayers == null) {
						// If world has not been specified then
						if (world == null) {
							// Assign players to all bukkit online players
							players = Bukkit.getOnlinePlayers();
							// Else if world has been specified - Then get all the players in that world
						} else {
							List<Player> worldPlayers = world.getPlayers();
							players = worldPlayers.toArray(new Player[worldPlayers.size()]);
						}
						// Else if players has been specified -  assign players to presetPlayers
					} else {
						players = presetPlayers;
					}

					// Create variables out of loop so variables aren't initiated on every loop through
					CPlayer cPlayer;
					Location lastSigLocation;
					Location playerLocation;
					Double locationDifference = 0d;

					// For all the players in the player variable
					for(Player player : players) {

						playerLocation = player.getLocation();

						if(!locationIsWithinRadiusOfCentre(playerLocation, centre, squaredRadiusFromLocation))
							continue;

						// Turn the player into a cPlayer
						cPlayer = Core.getOnlinePlayer(player);
						// Get the last significant location of that players
						lastSigLocation = lastSignificantLocation.get(cPlayer);

						// If the player doesn't have a significant location or
						// The players world is different to his last significant location or
						// The players last significant location's distance from
						// his current location is bigger than the default significant move distance then...
						if (lastSigLocation == null ||
								!playerLocation.getWorld().equals(lastSigLocation.getWorld()) ||
								(locationDifference = player.getLocation().distanceSquared(lastSigLocation)) > moveListener.getSquaredDefaultSignificantMoveDistance()) {

							// Turn the player into a cPlayer
							cPlayer = Core.getOnlinePlayer(player);
							// Make their new significant location where they stand
							lastSignificantLocation.put(cPlayer, playerLocation);

							// Run the significant move event for this listener
							moveListener.onSignificantMoveEvent(new CPlayerSignificantMoveEvent(cPlayer, locationDifference));
						}
					}
				}
			}.runTaskTimer(EntityAPI.getInstance(), 0, moveListener.getTimeDelay())
		);

		listenerList.add(moveListener);
	}

	private static boolean locationIsWithinRadiusOfCentre(@NonNull Location location, @NonNull Location centre, @Nullable Double squaredRadiusFromLocation) {
		return squaredRadiusFromLocation == null ||
			location.distanceSquared(centre) <= squaredRadiusFromLocation;
	}

	/**
	 * Un register a move listener
	 * @param moveListener The move listener
	 */
	@SneakyThrows
	public static void unRegisterListener(@NonNull CPlayerSignificantMoveListener moveListener) {
		if(!listenerList.contains(moveListener)) throw new SignificantMoveException("Someone tried to unregister a CPlayerSignificantMoveListener though it hasn't been registered yet!");
		listenerList.remove(moveListener);
		moveListener.getBukkitTaskId().cancel();
	}

	@Override
	public final void onPlayerLogin(final CPlayer player, InetAddress inetAddress) {
		// Made asynchronous as if there are a lot of listeners it slows down other things
		new BukkitRunnable() {
			@Override
			public void run() {
				for(CPlayerSignificantMoveListener listener : listenerList) {
					listener.getLastSignificantLocation().put(player, player.getBukkitPlayer().getLocation());
				}
			}
		}.runTaskAsynchronously(EntityAPI.getInstance());

	}

	@Override
	public final void onPlayerDisconnect(final CPlayer player) {
		// Made asynchronous as if there are a lot of listeners it slows down other things
		new BukkitRunnable() {
			@Override
			public void run() {
				for(CPlayerSignificantMoveListener listener : listenerList) {
					listener.getLastSignificantLocation().remove(player);
				}
			}
		}.runTaskAsynchronously(EntityAPI.getInstance());
	}

}
