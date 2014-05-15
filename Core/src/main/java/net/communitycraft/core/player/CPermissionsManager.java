package net.communitycraft.core.player;

import java.util.List;

public interface CPermissionsManager {
    CGroup createNewGroup(String name);
    CGroup getGroup(String name);
    void deleteGroup(CGroup group);
    List<CGroup> getGroups();
}
