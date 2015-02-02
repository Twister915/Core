package net.cogzmc.core.maps;

import com.sun.corba.se.spi.ior.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

@Data
public class CMongoMapManager implements CMapManager {
    private final static String MAPS_COLLECTION = "cmaps";
    private final static String MAPS_GRIDFS = "cmaps_files";
    private final static String MAP_ID_KEY = "uuid";

    private final CMongoDatabase database;
    @Getter(AccessLevel.NONE) private final Set<CMap> loadedMaps = new HashSet<>();
    private final GridFS bucket;

    public CMongoMapManager(CMongoDatabase database) {
        this.database = database;
        this.bucket = new GridFS(database.getMongoDatabase(), MAPS_GRIDFS);
    }

    @Override
    public Set<CMap> getMaps() {
        DBCollection mapsCollection = database.getCollection(MAPS_COLLECTION);
        Set<CMap> maps = new HashSet<>();
        maps.addAll(loadedMaps);
        for (DBObject dbObject : mapsCollection.find()) {
            maps.add(mapFromDBObject(dbObject));
        }
        return ImmutableSet.copyOf(maps);
    }

    @Override
    public Set<CMap> getLoadedMaps() {
        return ImmutableSet.copyOf(loadedMaps);
    }

    @Override
    public CMap getMapByID(UUID mapId) {
        DBObject one = database.getCollection(MAPS_COLLECTION).findOne(new BasicDBObject(MAP_ID_KEY, mapId.toString()));
        if (one == null) throw new IllegalArgumentException("The ID could not be located!");
        return mapFromDBObject(one);
    }

    @Override
    public CMap importWorld(World world) {
        UUID uuid = UUID.randomUUID();
        File tempFile = new File(CoreMaps.getInstance().getDataFolder(), String.valueOf(Core.getRandom().nextInt(10000)) + ".zip.tmp");
        ObjectId objectId;
        try {
            ZipFile zipFile = new ZipFile(tempFile);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            for (String s : world.getWorldFolder().list()) {
                File file = new File(world.getWorldFolder(), s);
                if (file.isDirectory()) zipFile.addFolder(file, parameters);
                else zipFile.addFile(file, parameters);
            }
            objectId = storeMap(zipFile.getFile());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
        CMap cMap = new CMap(uuid, world);
        cMap.setGridFSId(objectId);
        this.loadedMaps.add(cMap);
        database.getCollection(MAPS_COLLECTION).save(dbObjectFromMap(cMap));
        tempFile.delete();
        return cMap;
    }

    private ObjectId storeMap(File file) {
        try {
            GridFSInputFile file1 = bucket.createFile(file);
            file1.save();
            return (ObjectId) file1.get(MongoKey.ID_KEY.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final static String IDENTIFIER = "uuid";
    private final static String GRIDFS_FILE = "file";

    private CMap mapFromDBObject(DBObject object) {
        UUID uuid = UUID.fromString(MongoUtils.getValueFrom(object, IDENTIFIER, String.class));
        GridFSDBFile one = bucket.findOne(new BasicDBObject(MongoKey.ID_KEY.toString(), MongoUtils.getValueFrom(object, GRIDFS_FILE, ObjectId.class)));
        File tempFile;
        try {
            tempFile = new File(CoreMaps.getInstance().getDataFolder(), Core.getRandom().nextInt(100000) + ".zip.tmp");
            one.writeTo(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CMap cMap = new CMap(uuid, tempFile);
        cMap.setGridFSId(((ObjectId) one.get(MongoKey.ID_KEY.toString())));
        cMap.setMongoId(((ObjectId) object.get(MongoKey.ID_KEY.toString())));
        return cMap;
    }

    private DBObject dbObjectFromMap(CMap map) {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (map.getMongoId() != null) basicDBObject.put(MongoKey.ID_KEY.toString(), map.getMongoId());
        if (map.getGridFSId() != null) basicDBObject.put(GRIDFS_FILE, map.getGridFSId());
        basicDBObject.put(IDENTIFIER, map.getMapId().toString());
        return basicDBObject;
    }
}
