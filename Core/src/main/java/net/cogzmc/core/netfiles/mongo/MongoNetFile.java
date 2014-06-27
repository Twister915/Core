package net.cogzmc.core.netfiles.mongo;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.mongodb.gridfs.GridFSInputFile;
import lombok.Cleanup;
import lombok.NonNull;
import net.cogzmc.core.netfiles.NetDirectory;
import net.cogzmc.core.netfiles.NetFile;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


//Joe was here

/**
 * Check out {@link net.cogzmc.core.netfiles.mongo.MongoNetDirectory} for full explanation on how this API uses the GridFS.
 *
 * This represents a File within the GridFS.
 */
public class MongoNetFile extends MongoNetElement implements NetFile {
    protected MongoNetFile(GridFSDBFile me, GridFS fs) {
        super(me,fs);
    }

    @Override
    public void copyToLocalFile(File file) throws IOException {
        ((GridFSDBFile)getMe()).writeTo(file);
    }

    /**
     * Uploads to the remote GridFS from the local File, into the {@link net.cogzmc.core.netfiles.NetDirectory}, with the name passed.
     * @param localFile Location of the local FS file to upload to the remote GridFS
     * @param parent    Parent directory for the newly uploaded file
     * @param name      Name of the newly uploaded file
     * @return          The resulting {@link net.cogzmc.core.netfiles.mongo.MongoNetFile} that was created
     * @throws FileNotFoundException
     */
    public static MongoNetFile upload(@NonNull File localFile, @NonNull NetDirectory parent, @NonNull String name) throws FileNotFoundException {
        if (parent instanceof MongoNetDirectory) {
            if(name.equals("/")){
                throw new MongoIllegalFilename();
            }
            MongoNetDirectory mongoParent = (MongoNetDirectory) parent;
            @Cleanup FileInputStream fileInputStream = new FileInputStream(localFile);
            GridFSInputFile inputFile = mongoParent.getFs().createFile(fileInputStream, name, true);
            inputFile.save();
            MongoNetFile netFile = new MongoNetFile(mongoParent.getFs().findOne(((ObjectId) inputFile.getId())), mongoParent.getFs());
            parent.placeFile(netFile);
            return netFile;
        }
        throw new RuntimeException("Parent directory is not an instance of MongoNetDirectory! Cannot upload using this method.");
    }
}
