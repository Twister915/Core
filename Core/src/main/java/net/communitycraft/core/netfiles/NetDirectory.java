package net.communitycraft.core.netfiles;

import java.util.List;

public interface NetDirectory extends NetElement, Iterable<NetElement> {
    List<NetElement> getFiles();
    List<NetDirectory> getDirectories();
    List<NetElement> getContents();
}
