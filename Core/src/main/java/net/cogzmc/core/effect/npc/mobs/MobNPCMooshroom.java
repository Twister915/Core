package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.effect.npc.AbstractAnimalNNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCMooshroom extends AbstractAnimalNNPC {
    public MobNPCMooshroom(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.MUSHROOM_COW;
    }

    @Override
    public Float getMaximumHealth() {
        return 10F;
    }
}
