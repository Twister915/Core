package net.cogzmc.core.player;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface CGroup extends CPermissible {
    /**
     *
     * @return
     */
    List<CGroup> getParents();

    /**
     *
     * @param group
     */
    void addParent(CGroup group);

    /**
     *
     * @param group
     */
    void removeParent(CGroup group);

    /**
     *
     * @param group
     * @return
     */
    boolean isParent(CGroup group);

    /**
     *
     * @return
     */
    Map<String, Boolean> getAllPermissions();

    /**
     *
     * @return
     */
    List<CPlayer> getOnlineDirectMembers();

    /**
     *
     * @return
     */
    Integer getPriority();

    /**
     *
     * @param priority
     */
    void setPriority(Integer priority);
}
