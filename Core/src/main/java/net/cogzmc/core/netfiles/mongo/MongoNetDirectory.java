package net.cogzmc.core.netfiles.mongo;

import net.cogzmc.core.netfiles.NetDirectory;
import net.cogzmc.core.netfiles.NetElement;
import net.cogzmc.core.netfiles.NetFile;

import java.util.Iterator;
import java.util.List;

class MongoNetDirectory implements NetDirectory {

    @Override
    public List<NetFile> getFiles() {
        return null;
    }

    @Override
    public List<NetDirectory> getDirectories() {
        return null;
    }

    @Override
    public List<NetElement> getContents() {
        return null;
    }

    @Override
    public void placeFile(NetFile file) {

    }

    @Override
    public void containsFile(NetFile file) {

    }

    @Override
    public Iterator<NetElement> iterator() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }
}
