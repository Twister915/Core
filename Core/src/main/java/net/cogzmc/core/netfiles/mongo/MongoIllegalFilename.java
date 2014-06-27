package net.cogzmc.core.netfiles.mongo;

//Joe was here

/**
 * Is to be thrown when the code attempts to create a file with the '/' name. This name is to be reserved for the Root Filesystem.
 */
public class MongoIllegalFilename extends RuntimeException {
    public MongoIllegalFilename(){
        super("A NetFile's name may not be '/', since '/' is reserved for the Root FS.");
    }
}
