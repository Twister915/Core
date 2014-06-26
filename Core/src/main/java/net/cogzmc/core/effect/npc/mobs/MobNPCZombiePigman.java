package net.cogzmc.core.effect.npc.mobs;

import lombok.NonNull;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

public final class MobNPCZombiePigman extends MobNPCZombie {
    public MobNPCZombiePigman(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.PIG_ZOMBIE;
    }
}
