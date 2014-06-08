package net.cogzmc.permissions.commands.permissibile;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.CGroup;

import java.util.ArrayList;
import java.util.List;

public final class GroupResolutionDelegate implements PermissibleResolutionDelegate<CGroup> {
    @Override
    public CGroup getFor(String name) {
        return Core.getPermissionsManager().getGroup(name);
    }

    @Override
    public String getNameOfType() {
        return "Group";
    }

    @Override
    public List<String> getAutoCompleteFor(String s) {
        List<String> groupNames = new ArrayList<>();
        for (CGroup cGroup : Core.getPermissionsManager().getGroups()) {
            if (cGroup.getName().startsWith(s)) groupNames.add(s);
        }
        return groupNames;
    }
}
