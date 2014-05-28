package net.cogzmc.entityapi;

import lombok.Data;
import lombok.SneakyThrows;
import net.cogzmc.entityapi.entitites.FakeEntity;
import net.cogzmc.entityapi.entitites.FakeZombie;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 26/05/2014
 */

@Data
public class GFakeEntityManager implements FakeEntityManager {


	// Default render distance for entities
	public Double defaultRenderDistance = 30d;


	private Map<EntityType, Class<? extends FakeEntity>> entityTypeConversionMatrix = new HashMap<EntityType, Class<? extends FakeEntity>>() {
		{
			put(EntityType.ZOMBIE, FakeZombie.class);
		}
	};

	@SneakyThrows(Exception.class)
	public void spawnEntity(Location location, EntityType entityType) {
		Class<? extends FakeEntity> fakeEntity = entityTypeConversionMatrix.get(entityType);
		if(fakeEntity == null) throw new Exception(); //todo

		getPlayersThatCanSee(location);
	}

	private void getPlayersThatCanSee(Location location) {

	}

	@EventHandler
	void onPlayerMoveEvent(PlayerMoveEvent event) {

	}
}
