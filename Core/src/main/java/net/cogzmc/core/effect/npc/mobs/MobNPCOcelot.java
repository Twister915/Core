package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.effect.npc.AbstractTameableMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCOcelot extends AbstractTameableMobNPC {
    @NonNull private Ocelot.Type ocelotType = Ocelot.Type.WILD_OCELOT;

    public MobNPCOcelot(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.OCELOT;
    }

    @Override
    public Float getMaximumHealth() {
        return 10F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (!isTame() && (ocelotType == null || ocelotType.getId() != 1)) ocelotType = Ocelot.Type.WILD_OCELOT;
        dataWatcher.setObject(18, (byte)ocelotType.getId());
    }
}
