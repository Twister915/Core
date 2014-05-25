package net.cogzmc.hub;

import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.hub.items.HubItemsManager;
import net.cogzmc.hub.model.SettingsManager;
import net.cogzmc.hub.modules.HideStream;
import net.cogzmc.hub.modules.NoBuild;
import net.cogzmc.hub.modules.NoWeather;
import net.cogzmc.hub.modules.spawn.SetSpawn;
import net.cogzmc.hub.modules.spawn.Spawn;
import net.cogzmc.hub.modules.spawn.SpawnHandler;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/21/2014
 */
@ModuleMeta(
        name = "Hub",
        description = "The base hub API and plugin."
)
public final class Hub extends ModularPlugin {
    @Getter private static Hub instance;
    @Getter private SpawnHandler spawnHandler;
    @Getter private HubItemsManager itemsManager;
    @Getter private SettingsManager settingsManager;

    @Override
    protected void onModuleEnable() {
        //Create necessary instances
        Hub.instance = this;

        this.spawnHandler = new SpawnHandler();
        this.itemsManager = new HubItemsManager();
        getPlayerManager().registerCPlayerConnectionListener(this.itemsManager);
        this.settingsManager = new SettingsManager();

        /* commands */
        registerCommand(new SetSpawn());
        registerCommand(new Spawn());
        getPlayerManager().registerCPlayerConnectionListener(this.spawnHandler);

        /* listeners */
        registerListener(new NoWeather());
        registerListener(new HideStream());
        registerListener(new NoBuild());
    }

    @Override
    public void onModuleDisable() {
        this.settingsManager.save();
    }
}
