package net.cogzmc.permissions.command.impl.nouns;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.permissions.command.Noun;
import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.verbs.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public final class GroupNoun extends Noun<CGroup> {
    private final Set<Verb<CGroup>> verbs = new HashSet<>();
    private final String[] names = new String[]{"player", "user"};
    private final Class<CGroup> type = CGroup.class;

    {
        verbs.add(new PermSetVerb<CGroup>());
        verbs.add(new PermUnsetVerb<CGroup>());
        verbs.add(new GroupPurgeVerb());
        verbs.add(new PermSuffixVerb<CGroup>());
        verbs.add(new PermPrefixVerb<CGroup>());
        verbs.add(new PermTabColorVerb<CGroup>());
        verbs.add(new PermChatColorVerb<CGroup>());
        verbs.add(new GroupCreateVerb());
        verbs.add(new GroupSetParentVerb());
        verbs.add(new GroupRemoveParentVerb());
        verbs.add(new GroupSetPriorityVerb());
    }

    @Override
    protected List<String> getTabCompleteFor(String arg) {
        List<String> strings = new ArrayList<>();
        for (CGroup cGroup : Core.getPermissionsManager().getGroups()) {
            String name = cGroup.getName();
            if (name.toLowerCase().startsWith(arg.toLowerCase())) strings.add(name);
        }
        return strings;
    }

    @Override
    protected CGroup get(String s) {
        return Core.getPermissionsManager().getGroup(s);
    }
}
