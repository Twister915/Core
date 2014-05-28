package net.cogzmc.entityapi;

import com.avaje.ebeaninternal.server.deploy.BeanDescriptor.EntityType;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;

@ModuleMeta(
        name = "Entity API",
        description = "This provides control for all sorts of mobs."
)
public final class EntityAPI extends ModularPlugin {
    @Override
    protected void onModuleEnable() {

    }

	public void spawnFakeEntity(EntityType entityType) {

	}
}
