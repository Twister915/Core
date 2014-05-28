package net.cogzmc.entityapi.entitites;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 28/05/2014
 */
public class FakeZombie extends FakeEntity {
	private static final boolean debug = false; //TODO get debug mode
	private static final Logger log = null; //TODO get logger

	@Override
	public void showTo(Player player) {
		if (debug) {
			log.info("showTo() was called in class net.cogzmc.entityapi.entitites.FakeZombie! It Normally Returns void!");
		}
		// import org.apache.commons.lang.NotImplementedException;
		throw new NotImplementedException("showTo() has not been created yet in class net.cogzmc.entityapi.entitites.FakeZombie! It would Normally Return void!");
	}
}
