package net.cogzmc.core.netfiles;

import java.io.File;
import java.io.IOException;

public interface NetFile extends NetElement{
    void copyToLocalFile(File file) throws IOException;
}
