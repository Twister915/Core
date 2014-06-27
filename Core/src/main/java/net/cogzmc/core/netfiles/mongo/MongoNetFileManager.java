package net.cogzmc.core.netfiles.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSFile;
import net.cogzmc.core.netfiles.NetFileManager;
import net.cogzmc.core.player.mongo.CMongoDatabase;

//Joe was here

/**
 * Check out {@link net.cogzmc.core.netfiles.mongo.MongoNetDirectory} for full explanation on how this API uses the GridFS.
 *
 * This represents the root filesystem.
 */

public class MongoNetFileManager extends MongoNetDirectory implements NetFileManager {

    private MongoNetFileManager(GridFSFile me, GridFS fs){
        super(me,fs);
    }

    /**
     * Retrieves the FileManager for the passed {@link net.cogzmc.core.player.mongo.CMongoDatabase}. This uses the default bucket, 'fs'.
     * If a root directory does not exist, this method creates one.
     * @param database  Database to retrieve the Root Filesystem from
     * @return  The root filesystem.
     */
    public static MongoNetFileManager getFileManager(CMongoDatabase database){
        GridFS fs = new GridFS(database.getMongoDatabase());
        GridFSFile rootDirectory = fs.findOne(new BasicDBObject("filename","/"));
        if(rootDirectory == null){
            //May want to create a new one
            rootDirectory = fs.createFile("/");
            rootDirectory.save();
        }
        return new MongoNetFileManager(rootDirectory,fs);
    }

    /**
     * Like {@link ##getFileManager(net.cogzmc.core.player.mongo.CMongoDatabase)}, this method retrieves the FileManager for the passed {@link net.cogzmc.core.player.mongo.CMongoDatabase}.
     * However, this method does not use the default bucket, 'fs', but instead a bucket name passed
     * @param database  Database to retrieve the Root Filesystem from
     * @param alternateBucketName   Alternate bucket name to initialize the database on
     * @return  The root filesystem.
     */
    public static MongoNetFileManager getFileManager(CMongoDatabase database, String alternateBucketName){
        GridFS fs = new GridFS(database.getMongoDatabase(),alternateBucketName);
        GridFSFile rootDirectory = fs.findOne(new BasicDBObject("filename","/"));
        if(rootDirectory == null){
            //May want to create a new one
            rootDirectory = fs.createFile("/");
            rootDirectory.save();
        }
        return new MongoNetFileManager(rootDirectory,fs);
    }

}
