package net.cogzmc.core.effect.npc;

import lombok.NonNull;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

public final class NPCZombie extends AbstractGearNPC {
    public NPCZombie(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    EntityType getEntityType() {
        return EntityType.ZOMBIE;
    }
}
