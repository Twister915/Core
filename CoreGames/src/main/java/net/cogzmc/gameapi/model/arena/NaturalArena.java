package net.cogzmc.gameapi.model.arena;

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
