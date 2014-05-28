package net.cogzmc.entityapi;

import lombok.Data;
import lombok.SneakyThrows;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import net.cogzmc.entityapi.entitites.FakeEntity;
import net.cogzmc.entityapi.entitites.FakeZombie;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.net.InetAddress;
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

@Data
public class GFakeEntityManager implements FakeEntityManager {

	// Default render distance for entities
	public Double defaultRenderDistance = 30d;

	// Current entities alive
	public volatile List<FakeEntity> entities = new ArrayList<>();

	private Map<EntityType, Class<? extends FakeEntity>> entityTypeConversionMatrix = new HashMap<EntityType, Class<? extends FakeEntity>>() {
		{
			put(EntityType.ZOMBIE, FakeZombie.class);
		}
	};

	@SneakyThrows(Exception.class)
	@Override
	public void spawnEntity(Location location, EntityType entityType) {
		Class<? extends FakeEntity> fakeEntityClazz = entityTypeConversionMatrix.get(entityType);
		if(fakeEntityClazz == null) throw new Exception(); //todo

		FakeEntity fakeEntity = fakeEntityClazz.getConstructor(Location.class).newInstance(location);

		// The Player's that can see the entity
		Player[] players = getPlayersThatCanSee(location);

		for(Player player : players) {
			fakeEntity.showTo(player);
		}
		Core.getPlayerManager().registerCPlayerConnectionListener(new CPlayerConnectionListener() {
			@Override
			public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {}

			@Override
			public void onPlayerDisconnect(CPlayer player) {}
		});
	}

	/**
	 * Get Player's that can see an entity at a certain location
	 * @param location the certain location
	 */
	private Player[] getPlayersThatCanSee(Location location) {
		List<Player> players = new ArrayList<>();

		Double squaredDefaultRenderDistance = Math.pow(defaultRenderDistance, 2);
		for(Player player : location.getWorld().getPlayers()) {
			if(player.getLocation().distanceSquared(location) <= squaredDefaultRenderDistance)
				players.add(player);
		}

		return players.toArray(new Player[players.size()]);
	}

	/**
	 * Destroy a fakeEntity
	 * @param fakeEntity the fake entity to destroy
	 */
	@Override
	public void destroyEntity(FakeEntity fakeEntity) {

	}

	@EventHandler
	void onPlayerMoveEvent(PlayerMoveEvent event) {

	}
}
