package net.communitycraft.punishments;

import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.communitycraft.punishments.commands.PunishmentCommand;
import net.communitycraft.punishments.models.AbstractPunishment;

@ModuleMeta(
        name = "Punishments",
        description = "Stores all types of punishments for players."
)
public final class PunishmentModule extends ModularPlugin {

	@Getter private static PunishmentModule instance;

	@Getter private PunishmentManager punishmentManager;

	@Override
    public void onModuleEnable() {
		instance = this;
		punishmentManager = new PunishmentManager();
		for (Class<? extends AbstractPunishment> punishType : PunishmentManager.PUNISHMENT_CLASSES) {
			registerCommand(new PunishmentCommand(punishType, punishmentManager.getDelegate()));
		}
	}
}
