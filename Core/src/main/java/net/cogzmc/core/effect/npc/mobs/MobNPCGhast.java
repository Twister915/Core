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

@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCGhast extends AbstractMobNPC {
    private boolean attacking;

    public MobNPCGhast(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.GHAST;
    }

    @Override
    public Float getMaximumHealth() {
        return 10F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (attacking) dataWatcher.setObject(16, 1);
        else if (dataWatcher.getObject(16) != null) dataWatcher.removeObject(16);
    }
}
