package net.communitycraft.core.player;

import java.util.List;
import java.util.Map;

public interface CGroup extends CPermissible {
    String getName();
    List<CGroup> getParents();
    void addParent(CGroup group);
    void removeParent(CGroup group);
    boolean isParent(CGroup group);
    Map<String, Boolean> getAllPermissions();
    List<CPlayer> getOnlineDirectMembers();
}
