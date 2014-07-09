package net.cogzmc.entityapi;

import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.entityapi.entitites.FakeEntity;
import net.cogzmc.entityapi.entitites.FakeZombie;
import net.cogzmc.entityapi.sigmove.CPlayerSignificantMoveManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

@ModuleMeta(
        name = "Entity API",
        description = "This provides control for all sorts of mobs."
)
public final class EntityAPI extends ModularPlugin {

	@Getter
	private static EntityAPI instance;

	private static GFakeEntityManager fakeEntityManager;
	private FakeZombie fakeZombie;

	@Override
    protected void onModuleEnable() {
	    instance = this;

	    new CPlayerSignificantMoveManager();
	    fakeEntityManager = new GFakeEntityManager();

        // debug();
    }

	public FakeEntity spawnFakeEntity(Location location, EntityType entityType) {
		return fakeEntityManager.spawnEntity(location, entityType);
	}

	public void debug() {
		getLogger().info("Debug Method called - EntityAPI");
		fakeZombie = (FakeZombie) fakeEntityManager.spawnEntity(new Location(getServer().getWorlds().get(0), 0, 80, 0), EntityType.ZOMBIE);

		new BukkitRunnable() {

			@Override
			public void run() {
				getLogger().info("Observers: "+fakeZombie.getObservers().toString());
				getLogger().info("Possible Observers: "+fakeZombie.getPossibleObservers().toString());
				getLogger().info("Near Possible Observers: "+fakeZombie.getNearPossibleObservers().toString());
			}

		}.runTaskTimer(this, 0, 100);
	}
}
