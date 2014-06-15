package net.cogzmc.gameapi.model.arena;

import org.bukkit.World;

public interface Arena {
    void load();
    World getWorld();
    Iterable<Point> getSpawnPoints();
}
