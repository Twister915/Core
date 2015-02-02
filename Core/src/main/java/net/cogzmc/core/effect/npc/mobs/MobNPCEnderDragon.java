package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public final class MobNPCEnderDragon extends AbstractMobNPC {
    public MobNPCEnderDragon(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.ENDER_DRAGON;
    }

    @Override
    public Float getMaximumHealth() {
        return 200F;
    }
}
