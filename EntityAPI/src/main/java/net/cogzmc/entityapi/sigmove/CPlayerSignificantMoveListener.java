package net.cogzmc.entityapi.sigmove;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import org.bukkit.Location;
import org.bukkit.World;
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
public abstract class CPlayerSignificantMoveListener implements CPlayerConnectionListener {

	private final Double squaredDefaultSignificantMoveDistance;
	private final Integer timeDelay;
	private final World world;
	private final Player[] players;

	private BukkitTask bukkitTaskId;

	// The Last Significant locations mapped from player to significant location
	@Setter(AccessLevel.NONE)
	private volatile Map<CPlayer, Location> lastSignificantLocation = new HashMap<>();

	public CPlayerSignificantMoveListener(@NonNull Double squaredDefaultSignificantMoveDistance, @NonNull Integer timeDelay, World world, Player... players) {
		this.squaredDefaultSignificantMoveDistance = squaredDefaultSignificantMoveDistance;
		this.timeDelay = timeDelay;
		this.world = world;
		this.players = players;

		Core.getPlayerManager().registerCPlayerConnectionListener(this);
	}

	public abstract void onSignificantMoveEvent(CPlayerSignificantMoveEvent event);

}
