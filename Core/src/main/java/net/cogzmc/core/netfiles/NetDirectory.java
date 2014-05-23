package net.cogzmc.core.netfiles;

import java.util.List;

public interface NetDirectory extends NetElement, Iterable<NetElement> {
    List<NetFile> getFiles();
    List<NetDirectory> getDirectories();
    List<NetElement> getContents();

    void placeFile(NetFile file);
    void containsFile(NetFile file);
}
