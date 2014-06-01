package net.cogzmc.entityapi;

import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
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

    @Override
    protected void onModuleEnable() {
	    instance = this;

	    new CPlayerSignificantMoveManager();
	    fakeEntityManager = new GFakeEntityManager();
        debug();
    }

	public void spawnFakeEntity(EntityType entityType) {

	}

	public void debug() {
		getLogger().info("Debug Method called - EntityAPI");
		final FakeZombie fakeZombie = (FakeZombie) fakeEntityManager.spawnEntity(new Location(getServer().getWorlds().get(0), 0, 80, 0), EntityType.ZOMBIE);

		new BukkitRunnable() {

			@Override
			public void run() {
				getLogger().info(fakeZombie.getObservers().toString());
				getLogger().info(fakeZombie.getPossibleObservers().toString());
				getLogger().info(fakeZombie.getNearPossibleObservers().toString());
			}

		}.runTaskTimer(this, 0, 100);
	}
}
