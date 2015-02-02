package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class MobNPCSlime extends AbstractMobNPC {
    public final static class Size {
        public static final int SMALL = 1;
        public static final int NORMAL = 2;
        private static final int LARGE = 4;
    }

    private int size = 1;

    public MobNPCSlime(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.SLIME;
    }

    @Override
    public Float getMaximumHealth() {
        switch (size) {
            case 1:
                return 1f;
            case 2:
                return 4f;
            case 4:
                return 16f;
            default:
                return 16f;
        }
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        dataWatcher.setObject(16, (byte)size);
    }
}
