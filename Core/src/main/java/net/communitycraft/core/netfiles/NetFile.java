package net.communitycraft.core.netfiles;

import java.io.File;

public interface NetFile extends NetElement{
    void copyToLocalFile(File file);
            
}
