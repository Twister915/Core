package net.cogzmc.hub;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.hub.impl.*;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@ModuleMeta(
        name = "Hub",
        description = "The base hub API and plugin."
)
public final class Hub extends ModularPlugin {
    @Getter private static Hub instance;
    private final Set<Limitation> limitations = new HashSet<>();
    private Integer attemptedModuleEnables = 0;

    @Override
    protected void onModuleEnable() {
        //Create necessary instances
        Hub.instance = this;

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
        registerLimitation(new CreatureLimitation());

        logMessage("Enabled " + limitations.size() + "/" + attemptedModuleEnables + " limitations.");
    }

    public boolean registerLimitation(Limitation limitation) {
        attemptedModuleEnables++;
        try {
            limitation.enable();
        } catch (LimitationNotRequiredException e) {
            //What was a 'to do' figure out if LOWER_CAMEL vs UPPER_CAMEL makes a difference. :D
            //Well, in technicality, UPPER_CAMEL is the correct choice. UPPER_CAMEL should be used for class names, while LOWER_CAMEL is to be used for variable/field/method names.
            //BUT, since we are converting from this CaseFormat, and since it is going to be converted to lowercase anyhow... this should not make a difference. But lets play it safe,
            //And just use UPPER_CAMEL :D
            logMessage("Did not enable " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, limitation.getClass().getSimpleName()) + "! must set value " + limitation.getConfigKey() + " to true in your config.yml to enable this!");
            return false;
        }
        this.limitations.add(limitation);
        return true;
    }

    public ImmutableSet<Limitation> getEnabledLimitations() {
        return ImmutableSet.copyOf(limitations);
    }
}
