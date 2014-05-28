package net.cogzmc.entityapi.sigmove;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
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
public class CPlayerSignificantMoveManager implements CPlayerConnectionListener, Listener {

	// The Last Significant locations mapped from player to significant location
	private volatile Map<CPlayer, Location> lastSignificantLocation = new HashMap<>();

	// A List Of Listeners, listening into CPlayerSignificantMoveEvent
	private List<CPlayerSignificantMoveListener> listenerList = new ArrayList<>();

	// Significant Move Distance = 2 blocks (2 squared)
	public static Double squaredDefaultSignificantMoveDistance = 4d;

	public CPlayerSignificantMoveManager() {
		Core.getInstance().registerListener(this);
		Core.getPlayerManager().registerCPlayerConnectionListener(this);
	}

	/**
	 * Register a move listener
	 * REMEMBER: You can hook into the bukkit Event ({@link net.cogzmc.entityapi.sigmove.CPlayerSignificantMoveEvent})
	 * @param moveListener The move listener
	 */
	@SneakyThrows
	public void registerListener(@NonNull CPlayerSignificantMoveListener moveListener) {
		if(listenerList.contains(moveListener)) throw new SignificantMoveException("A CPlayerSignificantMoveListener was registered Twice!");
		listenerList.add(moveListener);
	}

	/**
	 * Un register a move listener
	 * @param moveListener The move listener
	 */
	@SneakyThrows
	public void unRegisterListener(@NonNull CPlayerSignificantMoveListener moveListener) {
		if(!listenerList.contains(moveListener)) throw new SignificantMoveException("Someone tried to unregister a CPlayerSignificantMoveListener though it hasn't been registered yet!");
		listenerList.add(moveListener);
	}

	/**
	 * Set the default distance till it is a, "significant", distance moved
	 * @param distance the default significant move distance
	 */
	public void setDefaultSignificantMoveDistance(Double distance) {
		squaredDefaultSignificantMoveDistance = Math.pow(distance, 2);
	}

	/**
	 * Get the default distance till it is a, "significant", distance moved
	 * @return the default significant move distance (squared, this is to avoid the hefty square root function in {@link #getDefaultSignificantMoveDistance()}
	 */
	public Double getSquaredDefaultSignificantMoveDistance() {
		return squaredDefaultSignificantMoveDistance;
	}

	/**
	 * Get the default distance till it is a, "significant", distance moved
	 * @return the default significant move distance (not squared)
	 * WARNING: This uses a hefty square root function, so if possible please use {@link #getSquaredDefaultSignificantMoveDistance()}
	 */
	public Double getDefaultSignificantMoveDistance() {
		return Math.sqrt(squaredDefaultSignificantMoveDistance);
	}

	void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cPlayer = Core.getOnlinePlayer(player);
		Location lastSigLocation = lastSignificantLocation.get(cPlayer);
		Double locationDiffrence = 0d;

		// If the player doesn't have a significant location or
		// The players last significant location's distance from
		// his current location is bigger than the default significant move distance then...
		if(lastSigLocation == null ||
				(locationDiffrence = player.getLocation().distanceSquared(lastSigLocation)) > squaredDefaultSignificantMoveDistance) {
			// Make their new significant location where they stand
			lastSignificantLocation.put(cPlayer, player.getLocation());
			callListenerList(cPlayer, locationDiffrence);
		}
	}

	private void callListenerList(CPlayer player, Double difference) {
		// The event
		CPlayerSignificantMoveEvent CPlayerSignificantMoveEvent = new CPlayerSignificantMoveEvent(player, difference);

		// Call the event via bukkit
		Bukkit.getPluginManager().callEvent(CPlayerSignificantMoveEvent);

		for(CPlayerSignificantMoveListener listener : listenerList) {
			// Call the event via independent listeners
			listener.onSignificantMoveEvent(CPlayerSignificantMoveEvent);
		}
	}

	@Override
	public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
		lastSignificantLocation.put(player, player.getBukkitPlayer().getLocation());
	}

	@Override
	public void onPlayerDisconnect(CPlayer player) {
		lastSignificantLocation.remove(player);
	}

	interface CPlayerSignificantMoveListener {
		public void onSignificantMoveEvent(CPlayerSignificantMoveEvent event);
	}
}
