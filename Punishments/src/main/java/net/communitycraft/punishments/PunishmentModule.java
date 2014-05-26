package net.communitycraft.punishments;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.communitycraft.punishments.models.AbstractPunishment;

@ModuleMeta(
        name = "Punishments",
        description = "Stores all types of punishments for players."
)
public final class PunishmentModule extends ModularPlugin {

	private PunishmentManager punishmentManager;

	@Override
    public void onModuleEnable() {
		AbstractPunishment.setDefaultPlayerManager(Core.getPlayerManager());
		punishmentManager = new PunishmentManager();
	}
}
