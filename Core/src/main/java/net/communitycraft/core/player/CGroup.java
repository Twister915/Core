package net.communitycraft.core.player;

import java.util.List;
import java.util.Map;

public interface CGroup extends CPermissable {
    String getName();
    List<CGroup> getParents();
    Map<String, Boolean> getAllPermissions();
    List<CPlayer> getOnlineDirectMembers();
}
