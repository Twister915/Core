package net.cogzmc.core.maps;

import org.bukkit.World;

import java.util.Set;
import java.util.UUID;

public interface CMapManager {
    Set<CMap> getMaps();
    Set<CMap> getLoadedMaps();
    CMap getMapByID(UUID mapId);
    CMap importWorld(World world);
    void updateMap(CMap toUpdate);

    boolean canUnlock(CMap map);
    boolean canLock(CMap map);
}