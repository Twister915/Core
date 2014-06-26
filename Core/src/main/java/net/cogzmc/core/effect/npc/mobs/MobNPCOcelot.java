package net.cogzmc.core.effect.npc.mobs;

import lombok.NonNull;
import lombok.Setter;
import net.cogzmc.core.effect.npc.AbstractTameableMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;

import java.util.Set;

public final class MobNPCOcelot extends AbstractTameableMobNPC {
    @Setter private Ocelot.Type ocelotType = Ocelot.Type.WILD_OCELOT;

    public MobNPCOcelot(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.OCELOT;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (!isTame() && (ocelotType == null || ocelotType.getId() != 1)) ocelotType = Ocelot.Type.WILD_OCELOT;
        dataWatcher.setObject(18, (byte)ocelotType.getId());
    }
}
