package net.cogzmc.entityapi;

import net.cogzmc.entityapi.entitites.FakeEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 28/05/2014
 */
public interface FakeEntityManager {

	/**
	 * Spawn an entity type at a certain location
	 * @param location the certain location
	 * @param entityType the entity type
	 */
	public FakeEntity spawnEntity(Location location, EntityType entityType);

	/**
	 * Destroy a fake entity
	 * @param fakeEntity the fake entity to destroy
	 */
	public void destroyEntity(FakeEntity fakeEntity);
}
