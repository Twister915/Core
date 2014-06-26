package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;
import net.cogzmc.core.effect.npc.AbstractGearMobNPC;
import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public final class MobNPCZombie extends AbstractGearMobNPC {
    private boolean villager;
    private boolean child;
    private boolean converting;

    public MobNPCZombie(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.ZOMBIE;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (child) dataWatcher.setObject(12, (byte)1);
        else if (dataWatcher.getObject(12) != null) dataWatcher.removeObject(12);

        if (villager) dataWatcher.setObject(13, (byte)1);
        else if (dataWatcher.getObject(13) != null) dataWatcher.removeObject(13);

        if (converting) dataWatcher.setObject(14, (byte)1);
        else if (dataWatcher.getObject(14) != null) dataWatcher.removeObject(14);
    }
}
