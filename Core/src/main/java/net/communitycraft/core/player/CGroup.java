package net.communitycraft.core.player;

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
    String getName();

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
}
