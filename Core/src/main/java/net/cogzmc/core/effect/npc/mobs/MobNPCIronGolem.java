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
public final class MobNPCIronGolem extends AbstractMobNPC {
    private boolean playerCreated;

    public MobNPCIronGolem(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.IRON_GOLEM;
    }

    @Override
    protected Float getMaximumHealth() {
        return 100F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (playerCreated) dataWatcher.setObject(16, (byte)1);
        else if (dataWatcher.getObject(16) != null) dataWatcher.removeObject(16);
    }
}
