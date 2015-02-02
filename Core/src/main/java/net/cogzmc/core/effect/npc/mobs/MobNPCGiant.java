package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCGiant extends AbstractMobNPC {
    public MobNPCGiant(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.GIANT;
    }

    @Override
    public Float getMaximumHealth() {
        return 100f;
    }
}
