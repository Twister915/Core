package net.cogzmc.coreessentials;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.effect.particle.ParticleEffect;
import net.cogzmc.core.effect.particle.ParticleEffectType;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.coreessentials.commands.*;
import net.cogzmc.coreessentials.server.ServerCommand;
import net.cogzmc.coreessentials.signs.ColoredSigns;

@ModuleMeta(
        name = "Core Essentials",
        description = "Provides essential end user commands and overrides for Bukkit that Core cannot properly " +
                "provide (as it is not a module). This plugin *should* be included on any running Core server " +
                "instances."
)
public final class CoreEssentials extends ModularPlugin {
    @Getter private TabColorManager tabColorManager;

    @Override
    protected void onModuleEnable() {
        registerCommand(new PluginsCommand());
        registerCommand(new NickNameCommand());
        registerCommand(new LagInfoCommand());
        if (Core.getNetworkManager() != null) registerCommand(new ServerCommand());
        tabColorManager = new TabColorManager();
        Core.getPermissionsManager().registerObserver(tabColorManager);
        registerListener(tabColorManager);
        registerListener(new ColoredSigns());
    }
}
