package net.cogzmc.entityapi;

import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.entityapi.sigmove.CPlayerSignificantMoveManager;
import org.bukkit.entity.EntityType;

@ModuleMeta(
        name = "Entity API",
        description = "This provides control for all sorts of mobs."
)
public final class EntityAPI extends ModularPlugin {

	@Getter
	private static EntityAPI instance;

    @Override
    protected void onModuleEnable() {
	    instance = this;

	    new CPlayerSignificantMoveManager();
	    new GFakeEntityManager();
    }

	public void spawnFakeEntity(EntityType entityType) {

	}
}
