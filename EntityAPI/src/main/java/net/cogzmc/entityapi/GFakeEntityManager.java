package net.cogzmc.entityapi;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.entityapi.entitites.FakeEntity;
import net.cogzmc.entityapi.entitites.FakeZombie;
import net.cogzmc.entityapi.sigmove.CPlayerSignificantMoveEvent;
import net.cogzmc.entityapi.sigmove.CPlayerSignificantMoveListener;
import net.cogzmc.entityapi.sigmove.CPlayerSignificantMoveManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

/**
 * <p>
 * <h2>Observers:</h2>
 * There are 3 types of observers:
 * <table border="1" cellpadding="4">
 *     <tbody>
 *         <tr>
 *             <th>Observer Type</th>
 *             <th>What Do They Do?</th>
 *         </tr>
 *         <tr>
 *             <td>Observer</td>
 *             <td>An observer is someone who can see the entity. Someone who is able to see the entity and can currently see the entity</td>
 *         </tr>
 *         <tr>
 *             <td>Possible Observer</td>
 *             <td>An observer who could see the entity if he was let, though has not been let yet. He is in the render distance of the entity.</td>
 *         </tr>
 *         <tr>
 *             <td>Near Possible Observer</td>
 *             <td>An observer that is near the render distance to becoming a possible observer. This only exists as a buffer area to make code more efficient. Please refer to the image below.</td>
 *         </tr>
 *     </tbody>
 * </table>
 * <img src="http://i.imgur.com/gDdyhkq.png" width="350" height="250"/>
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 26/05/2014
 */
public class GFakeEntityManager implements FakeEntityManager, Listener {

	// Default render distance for entities 30 blocks (squared so 900) - 30*30
	public Double squaredDefaultRenderDistance = 900d;

	// How many blocks out of the render distance is a player still a near observer
	// (30 blocks + 10 blocks)^2 = (40)^2
	public Double squaredBufferDistance = 1600d;

	// Current entities alive
	@Getter
	private volatile List<FakeEntity> entities = new ArrayList<>();

	/**
	 * A Matrix converting Bukkit {@link org.bukkit.entity.EntityType} to FakeEntity classes
	 * Treated as a constant though this is not really a constant so constant naming standard is not used
	 */
	@Getter
	private final Map<EntityType, Class<? extends FakeEntity>> entityTypeConversionMatrix = new HashMap<EntityType, Class<? extends FakeEntity>>() {
		{
			put(EntityType.ZOMBIE, FakeZombie.class);
		}
	};

	private volatile Integer entityIDCount = 1000;

	public GFakeEntityManager() {
		Bukkit.getPluginManager().registerEvents(this, EntityAPI.getInstance());

		final GFakeEntityManager instance = this;

		CPlayerSignificantMoveManager.registerListener(new CPlayerSignificantMoveListener(8d, 10) {
			@Override
			public void onSignificantMoveEvent(CPlayerSignificantMoveEvent event) {
				instance.onBufferDistanceMove(event);
			}
		});
		CPlayerSignificantMoveManager.registerListener(new CPlayerSignificantMoveListener(2d, 5) {
			@Override
			public void onSignificantMoveEvent(CPlayerSignificantMoveEvent event) {
				instance.onRenderDistanceMove(event);
			}
		});
	}

	@Override
	public List<FakeEntity> getNearEntities(Location location, Double squaredDistance) {
		List<FakeEntity> entities = new ArrayList<>();

		for(FakeEntity entity : getEntitiesInWorld(location.getWorld())) {
			if(entity.getLocation().distanceSquared(location) <= squaredDistance)
				entities.add(entity);
		}

		return entities;
	}

	@Override
	public synchronized FakeEntity getEntityByUUID(@NonNull UUID uuid) {
		for(FakeEntity fakeEntity : entities) {
			if(fakeEntity.getUuid().equals(uuid))
				return fakeEntity;
		}
		return null;
	}

	private void onBufferDistanceMove(CPlayerSignificantMoveEvent event) {
		CPlayer player = event.getPlayer();
		Location location = player.getBukkitPlayer().getLocation();

		for(FakeEntity entity : getNearEntities(location, squaredBufferDistance)) {
			if(!isInBufferDistance(location.distanceSquared(entity.getLocation()))) continue;

			entity.addNearPossibleObserver(player);
		}
	}

	private void onRenderDistanceMove(CPlayerSignificantMoveEvent event) {
		CPlayer player = event.getPlayer();
		Location location = player.getBukkitPlayer().getLocation();

		for(FakeEntity entity : getNearEntities(location, squaredDefaultRenderDistance)) {
			if(entity.isNearPossibleObserver(player))
				entity.removeNearPossibleObserver(player);

			entity.addPossibleObserver(player);
		}
	}

	@SneakyThrows
	@Override
	public FakeEntity spawnEntity(Location location, EntityType entityType) {
		Class<? extends FakeEntity> fakeEntityClazz = entityTypeConversionMatrix.get(entityType);
		if(fakeEntityClazz == null) throw new Exception(); //todo

		FakeEntity fakeEntity = fakeEntityClazz.getConstructor(Location.class).newInstance(location);

		fakeEntity.setEntityID(entityIDCount++);

		fakeEntity.setUuid(UUID.randomUUID());

		fakeEntity.addNearPossibleObservers(getNearPossibleObservers(location));

		// The Player's that can see the entity
		fakeEntity.addPossibleObservers(getPossibleObservers(location));

		return fakeEntity;
	}

	private boolean isInBufferDistance(Double distanceSquared) {
		return isWithinBufferDistance(distanceSquared) &&
				distanceSquared > squaredDefaultRenderDistance;
	}

	private boolean isWithinBufferDistance(Double distanceSquared) {
		return distanceSquared <= squaredBufferDistance;
	}

	private boolean isWithinRenderDistance(Double distanceSquared) {
		return distanceSquared <= squaredDefaultRenderDistance;
	}

	/**
	 * Get Player's that could possibly see an entity at a certain location
	 * @param location the certain location
	 * @return a list of those players
	 */
	private List<CPlayer> getPossibleObservers(Location location) {
		List<CPlayer> players = new ArrayList<>();

		CPlayer cPlayer;
		// For every player in that world
		for(Player player : location.getWorld().getPlayers()) {
			// If the players distance from the location (squared) is less or equal than the default render distance (squared) then
			if(player.getLocation().distanceSquared(location) <= squaredDefaultRenderDistance) {
				// Turn bukkit player into CPlayer
				cPlayer = Core.getPlayerManager().getCPlayerForPlayer(player);
				// Add the CPlayer to the players that can see
				players.add(cPlayer);
			}
		}

		return players;
	}

	/**
	 * Get Player's that are just outside the render distance (in the buffer distance)
	 * @param location the certain location
	 * @return a list of those players
	 */
	private List<CPlayer> getNearPossibleObservers(Location location) {
		List<CPlayer> players = new ArrayList<>();

		CPlayer cPlayer;

		// For every player in that world
		for(Player player : location.getWorld().getPlayers()) {

			Double distanceSquared = player.getLocation().distanceSquared(location);

			// If the players distance from the location (squared) is less or than the default render distance (squared) then
			if(isInBufferDistance(distanceSquared)) {
				// Turn bukkit player into CPlayer
				cPlayer = Core.getPlayerManager().getCPlayerForPlayer(player);
				// Add the CPlayer to the players that can see
				players.add(cPlayer);
			}
		}

		return players;
	}

	/**
	 * Set the default distance till it an entity can possibly be observed
	 * @param distance the default render distance for entities
	 */
	public void setDefaultRenderDistance(Double distance) {
		squaredDefaultRenderDistance = Math.pow(distance, 2);
	}

	/**
	 * Get the default distance till it an entity can possibly be observed
	 * @return the default render distance for entities (squared, this is to avoid the hefty square root function in {@link #getDefaultRenderDistance()}
	 */
	public Double getSquaredDefaultRenderDistance() {
		return squaredDefaultRenderDistance;
	}

	/**
	 * Get the default distance till it an entity can possibly be observed
	 * @return the default render distance for entities (not squared)
	 * WARNING: This uses a hefty square root function, so if possible please use {@link #getSquaredDefaultRenderDistance()}
	 */
	public Double getDefaultRenderDistance() {
		return Math.sqrt(squaredDefaultRenderDistance);
	}

	/**
	 * Destroy a fakeEntity
	 * @param fakeEntity the fake entity to destroy
	 */
	@Override
	public void destroyEntity(FakeEntity fakeEntity) {

	}

	/**
	 * Convenience method to get entities in a world
	 * @param world the world get the fake entities in
	 * @return the fake entities in that world
	 */
	private synchronized List<FakeEntity> getEntitiesInWorld(World world) {
		List<FakeEntity> entities = new ArrayList<>();
		for(FakeEntity fakeEntity : entities) {
			if(fakeEntity.getLocation().getWorld().equals(world))
				entities.add(fakeEntity);
		}
		return entities;
	}
}
