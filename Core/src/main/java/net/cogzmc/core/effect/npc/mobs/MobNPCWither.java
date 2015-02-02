package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public final class MobNPCWither extends AbstractMobNPC {
    private Integer target1id, target2id, target3id, inculnerableTime;

    public MobNPCWither(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.WITHER;
    }

    @Override
    public Float getMaximumHealth() {
        return 300f;
    }

    @Override
    protected void onDataWatcherUpdate() {
        if (target1id != null) dataWatcher.setObject(17, target1id);
        else if (dataWatcher.getObject(17) != null) dataWatcher.removeObject(17);
        if (target2id != null) dataWatcher.setObject(18, target2id);
        else if (dataWatcher.getObject(18) != null) dataWatcher.removeObject(18);
        if (target3id != null) dataWatcher.setObject(19, target3id);
        else if (dataWatcher.getObject(19) != null) dataWatcher.removeObject(19);
        dataWatcher.setObject(20, inculnerableTime);
    }
}
