package net.communitycraft.base;

import net.communitycraft.base.command.WorldCommand;
import net.communitycraft.core.modular.ModularPlugin;

public final class BasePackageModule extends ModularPlugin {
    @Override
    public void onModuleEnable() {
        addCommand(new WorldCommand());
    }
}
