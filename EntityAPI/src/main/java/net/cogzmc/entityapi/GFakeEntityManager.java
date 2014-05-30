package net.cogzmc.entityapi;

import lombok.Getter;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
 * @since 26/05/2014
 */
public class GFakeEntityManager implements FakeEntityManager, Listener {

	// Default render distance for entities 30 blocks (squared so 900) - 30*30
	public Double squaredDefaultRenderDistance = 900d;

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

	public GFakeEntityManager() {
		Bukkit.getPluginManager().registerEvents(this, EntityAPI.getInstance());
		CPlayerSignificantMoveManager.registerListener();
	}

	@SneakyThrows
	@Override
	public void spawnEntity(Location location, EntityType entityType) {
		Class<? extends FakeEntity> fakeEntityClazz = entityTypeConversionMatrix.get(entityType);
		if(fakeEntityClazz == null) throw new Exception(); //todo

		FakeEntity fakeEntity = fakeEntityClazz.getConstructor(Location.class).newInstance(location);

		// The Player's that can see the entity
		fakeEntity.addPossibleObservers(getPossibleObservers(location));
	}

	/**
	 * Get Player's that can see an entity at a certain location
	 * @param location the certain location
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

	void onCPlayerSignificantMoveEvent() {


	/*	CPlayer player = event.getPlayer();

		getEntitiesInWorld(event.getPlayer().getBukkitPlayer().getWorld());

		// For every player in that world
		for(Player player : location.getWorld().getPlayers()) {
			// If the players distance from the location (squared) is less or equal than the default render distance (squared) then
			if(player.getLocation().distanceSquared(location) <= squaredDefaultRenderDistance) {
				// Turn bukkit player into CPlayer
				cPlayer = Core.getPlayerManager().getCPlayerForPlayer(player);
				// Add the CPlayer to the players that can see
				players.add(cPlayer);
			}
		}*/
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
	}
}
