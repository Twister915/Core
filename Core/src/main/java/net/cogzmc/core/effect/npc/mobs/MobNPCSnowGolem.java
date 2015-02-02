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
public final class MobNPCSnowGolem extends AbstractMobNPC {
    public MobNPCSnowGolem(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.SNOWMAN;
    }

    @Override
    public Float getMaximumHealth() {
        return 4F;
    }
}
