package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractGearMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Data
public final class MobNPCSkeleton extends AbstractGearMobNPC {
    private boolean wither = false;

    public MobNPCSkeleton(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.SKELETON;
    }

    @Override
    public Float getMaximumHealth() {
        return 20F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (wither) dataWatcher.setObject(13, (byte)1);
        else if (dataWatcher.getObject(13) !=  null) dataWatcher.removeObject(13);
    }
}
