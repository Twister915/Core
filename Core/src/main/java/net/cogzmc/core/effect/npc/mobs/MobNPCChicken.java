package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractAnimalNNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCChicken extends AbstractAnimalNNPC {
    public MobNPCChicken(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.CHICKEN;
    }

    @Override
    public Float getMaximumHealth() {
        return 4f;
    }
}
