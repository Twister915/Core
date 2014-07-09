package net.cogzmc.entityapi.entitites;

import lombok.extern.java.Log;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Location;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 28/05/2014
 */
@Log
public class FakeZombie extends FakeEntity {

	public FakeZombie(Location location) {
		super(location);
	}

	@Override
	protected void showTo(CPlayer observer) {
		log.info("Fake Zombie shown to "+observer.getName());
	}

	@Override
	protected void removeFor(CPlayer observer) {
		log.info("Fake Zombie removed for "+observer.getName());
	}
}
