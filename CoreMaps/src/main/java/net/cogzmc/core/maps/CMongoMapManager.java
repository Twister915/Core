package net.cogzmc.core.maps;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import lombok.Data;
import net.cogzmc.core.player.mongo.CMongoDatabase;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class CMongoMapManager implements CMapManager {
    private final static String MAPS_COLLECTION = "cmaps";
    private final static String MAP_ID_KEY = "uuid";

    private final CMongoDatabase database;
    private final Set<CMap> loadedMaps = new HashSet<>();

    @Override
    public Set<CMap> getMaps() {
        DBCollection mapsCollection = database.getCollection(MAPS_COLLECTION);
        Set<CMap> maps = new HashSet<>();
        maps.addAll(loadedMaps);
        for (DBObject dbObject : mapsCollection.find()) {
            maps.add(mapFromDBObject(dbObject));
        }
        return maps;
    }

    @Override
    public CMap getMapByID(UUID mapId) {
        DBObject one = database.getCollection(MAPS_COLLECTION).findOne(new BasicDBObject(MAP_ID_KEY, mapId.toString()));
        if (one == null) throw new IllegalArgumentException("The ID could not be located!");
        return mapFromDBObject(one);
    }

    @Override
    public CMap importWorld(World world) {
        return null;
    }

    @Override
    public void updateMap(CMap toUpdate) {

    }

    @Override
    public boolean canUnlock(CMap map) {
        return false;
    }

    @Override
    public boolean canLock(CMap map) {
        return false;
    }

    private static CMap mapFromDBObject(DBObject object) {
        return null;
    }

    private static DBObject dbObjectFromMap(CMap map) {
        return null;
    }
}
