package net.cogzmc.core.maps;

import java.util.Set;
import java.util.UUID;

public interface CMapManager {
    Set<CMap> getMaps();
    Set<CMap> getLoadedMaps();
    CMap getMapByID(UUID mapId);
    CMap importWorld(World world);
}