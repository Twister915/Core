package net.cogzmc.entityapi.sigmove;

import lombok.Data;
import net.cogzmc.core.player.CPlayer;

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
public class CPlayerSignificantMoveEvent {

	private final CPlayer player;
	private final Double squaredDistanceMoved;

}