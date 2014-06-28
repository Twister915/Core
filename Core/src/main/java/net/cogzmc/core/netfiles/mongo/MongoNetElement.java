package net.cogzmc.core.netfiles.mongo;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.cogzmc.core.netfiles.NetElement;

//Joe was here

/**
 * Represents a basic Mongo Directory/File.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MongoNetElement implements NetElement {

    @Getter(AccessLevel.PACKAGE)
    private GridFSFile me;

    @Getter(AccessLevel.PACKAGE)
    private GridFS fs;

    @Override
    public String getName() {
        return me.getFilename();
    }

    @Override
    public String getId() {
        return me.getId().toString();
    }

    protected boolean isDirectory(){
        return me.getMetaData().containsField("children");
    }
}
