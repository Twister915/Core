package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public final class MobNPCBlaze extends AbstractMobNPC {
    private boolean onFire;

    public MobNPCBlaze(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.BLAZE;
    }

    @Override
    protected Float getMaximumHealth() {
        return 20F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (onFire) dataWatcher.setObject(16, (byte)1);
        else if (dataWatcher.getObject(16) != null) dataWatcher.removeObject(16);
    }
}
