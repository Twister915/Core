package net.cogzmc.gameapi.model.arena;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.Arrays;

public final class NaturalArena extends BaseArena {
    @Override
    World createOrLoadWorld() {
        return WorldCreator.
                name(ArenaUtils.createRandomWorldName()).
                environment(World.Environment.NORMAL).
                type(WorldType.NORMAL).
                createWorld();
    }
}
