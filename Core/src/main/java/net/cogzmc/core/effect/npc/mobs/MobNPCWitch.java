package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class MobNPCWitch extends AbstractMobNPC {
    private boolean agressive;

    public MobNPCWitch(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.WITCH;
    }

    @Override
    public Float getMaximumHealth() {
        return 26F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (agressive) dataWatcher.setObject(21, 1);
        else if (dataWatcher.getObject(21) != null) dataWatcher.removeObject(21);
    }
}
