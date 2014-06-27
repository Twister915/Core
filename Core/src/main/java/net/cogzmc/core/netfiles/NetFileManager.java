package net.cogzmc.core.netfiles;

/**
 * This represents both the manager for all {@link net.cogzmc.core.netfiles.NetFile}s and {@link net.cogzmc.core.netfiles.NetDirectory}s, it also serves as a root {@link net.cogzmc.core.netfiles.NetDirectory}.
 */
public interface NetFileManager extends NetDirectory {

    /**
     * Used for looking up a specific file within the Root filesystem
     * @param id    ID of object to lookup
     * @return      The found NetElement, if it exists. Otherwise, return null.
     */
    public NetElement lookupNetElement(String id);

}
