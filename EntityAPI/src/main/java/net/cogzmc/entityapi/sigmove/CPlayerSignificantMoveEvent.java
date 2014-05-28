package net.cogzmc.entityapi.sigmove;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An Optimised Move Event that only fires on significant moves
 * <p/>
 * Latest Change: Created it
 * <p/>
 *
 * @author George
 * @since 29/05/2014
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class CPlayerSignificantMoveEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final CPlayer player;
	private final Double squaredDistanceMoved;

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
