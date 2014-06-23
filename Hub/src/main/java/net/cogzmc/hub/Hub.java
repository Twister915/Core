package net.cogzmc.hub;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.hub.items.HubItem;
import net.cogzmc.hub.items.HubItemsManager;
import net.cogzmc.hub.limitations.Limitation;
import net.cogzmc.hub.limitations.LimitationNotRequiredException;
import net.cogzmc.hub.limitations.impl.*;
import net.cogzmc.hub.model.SettingsManager;
import net.cogzmc.hub.spawn.SetSpawn;
import net.cogzmc.hub.spawn.Spawn;
import net.cogzmc.hub.spawn.SpawnHandler;

import java.util.HashSet;
import java.util.Set;

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
    private final Set<Limitation> limitations = new HashSet<>();
    private Integer attemptedModuleEnables = 0;

    @Override
    protected void onModuleEnable() {
        //Create necessary instances
        Hub.instance = this;

        this.spawnHandler = new SpawnHandler();
        this.itemsManager = new HubItemsManager();
        registerListener(this.itemsManager);
        this.settingsManager = new SettingsManager();

        /* commands */
        registerCommand(new SetSpawn());
        registerCommand(new Spawn());

        /* listeners */
		registerListener(this.spawnHandler);
		registerListener(HubItem.EventDispatcher.getInstance());

        /* limitations */
        registerLimitation(new BuildLimitation());
        registerLimitation(new InventoryLimitation());
        registerLimitation(new StreamLimitation());
        registerLimitation(new WeatherLimitation());
        registerLimitation(new DropLimitation());
        registerLimitation(new PvPLimitation());
        registerLimitation(new DamageLimitation());
        registerLimitation(new HungerLimitation());
        registerLimitation(new VoidLimitation());

        logMessage("Enabled " + limitations.size() + "/" + attemptedModuleEnables + " limitations.");
    }

    public boolean registerLimitation(Limitation limitation) {
        attemptedModuleEnables++;
        try {
            limitation.enable();
        } catch (LimitationNotRequiredException e) {
            //TODO figure out if LOWER_CAMEL vs UPPER_CAMEL makes a difference. :D
            logMessage("Did not enable " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, limitation.getClass().getSimpleName()) + "! must set value " + limitation.getConfigKey() + " to true in your config.yml to enable this!");
            return false;
        }
        this.limitations.add(limitation);
        return true;
    }

    public ImmutableSet<Limitation> getEnabledLimitations() {
        return ImmutableSet.copyOf(limitations);
    }

    @Override
    public void onModuleDisable() {
        this.settingsManager.save();
    }
}
