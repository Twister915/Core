package net.cogzmc.entityapi.sigmove;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 29/05/2014
 */
@Data
public abstract class CPlayerSignificantMoveListener {

	private final Double squaredDefaultSignificantMoveDistance;
	private final Integer timeDelay;
	private final Location location;
	private final Double squaredRadiusFromLocation;
	private final Player[] players;

	private BukkitTask bukkitTaskId;

	@Setter(AccessLevel.NONE)
	private Double squaredDistance;

	// The Last Significant locations mapped from player to significant location
	@Setter(AccessLevel.NONE)
	private volatile Map<CPlayer, Location> lastSignificantLocation = new HashMap<>();

	public CPlayerSignificantMoveListener(@NonNull Double distance, @NonNull Integer timeDelay, Player... players) {
		this(distance, timeDelay, null, null, players);
	}

	public CPlayerSignificantMoveListener(@NonNull Double distance, @NonNull Integer timeDelay, Location location) {
		this(distance, timeDelay, location, null);
	}

	public CPlayerSignificantMoveListener(@NonNull Double significantMoveDistance, @NonNull Integer timeDelay, Location location, Double radiusFromLocation, Player... players) {
		this.squaredDefaultSignificantMoveDistance = Math.pow(significantMoveDistance, 2);
		this.timeDelay = timeDelay;
		this.location = location;
		this.squaredRadiusFromLocation = radiusFromLocation == null ? null : Math.pow(radiusFromLocation, 2);
		this.players = players;
	}

	public abstract void onSignificantMoveEvent(CPlayerSignificantMoveEvent event);
}
