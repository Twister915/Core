package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractAgeableMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public final class MobNPCCreeper extends AbstractAgeableMobNPC {
    private boolean charged;
    private boolean fused;

    public MobNPCCreeper(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.CREEPER;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        dataWatcher.setObject(16, (byte)(fused ? 1 : -1));
        if (charged) dataWatcher.setObject(17, (byte)1);
        else if (dataWatcher.getObject(17) != null) dataWatcher.removeObject(17);
    }
}
