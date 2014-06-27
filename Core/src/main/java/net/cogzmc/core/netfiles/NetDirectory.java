package net.cogzmc.core.netfiles;

import java.util.List;

public interface NetDirectory extends NetElement, Iterable<NetElement> {
    List<NetFile> getFiles();
    List<NetDirectory> getDirectories();
    List<NetElement> getContents();

    void placeFile(NetFile file);
    boolean containsFile(NetFile file);

    NetDirectory createNewDirectory(String name);
}
