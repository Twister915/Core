package net.cogzmc.core.netfiles.mongo;

import com.mongodb.gridfs.GridFS;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.cogzmc.core.netfiles.NetElement;
import org.bson.types.ObjectId;

import java.util.Iterator;

/**
 * An iterator for NetElements when passed a ID list, and a filesystem to retrieve the files from.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class MongoChildrenIterator implements Iterator<NetElement> {

    Iterator<ObjectId> ids;

    GridFS fileSystem;

    @Override
    public boolean hasNext() {
        return ids.hasNext();   //If we have more IDs to process, then we have more files to retrieve.
    }

    @Override
    public NetElement next() {
        return new MongoNetElement(fileSystem.find(ids.next()),fileSystem); //Find the NetFile, and then wrap it with a MongoNetElement, along with the fileSystem.
    }
}
