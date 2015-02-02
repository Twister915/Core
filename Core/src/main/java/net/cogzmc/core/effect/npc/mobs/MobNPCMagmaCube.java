package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCMagmaCube extends MobNPCSlime {
    public MobNPCMagmaCube(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.MAGMA_CUBE;
    }
}
