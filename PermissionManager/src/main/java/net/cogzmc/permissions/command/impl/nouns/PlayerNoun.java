package net.cogzmc.permissions.command.impl.nouns;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.permissions.command.Noun;
import net.cogzmc.permissions.command.Verb;
import net.cogzmc.permissions.command.impl.verbs.*;

import java.util.*;

@Getter
public final class PlayerNoun extends Noun<COfflinePlayer> {
    private final Set<Verb<COfflinePlayer>> verbs = new HashSet<>();
    private final String[] names = new String[]{"player", "user"};
    private final Class<COfflinePlayer> type = COfflinePlayer.class;

    {
        verbs.add(new PermSetVerb<COfflinePlayer>());
        verbs.add(new PermUnsetVerb<COfflinePlayer>());
        verbs.add(new PermChatColorVerb<COfflinePlayer>());
        verbs.add(new PermPrefixVerb<COfflinePlayer>());
        verbs.add(new PermSuffixVerb<COfflinePlayer>());
        verbs.add(new PermTabColorVerb<COfflinePlayer>());
        verbs.add(new PermUnsetVerb<COfflinePlayer>());
        verbs.add(new PlayerPurgeVerb());
        verbs.add(new PlayerAddGroupVerb());
        verbs.add(new PlayerDelGroupVerb());
        verbs.add(new PlayerSetGroupVerb());
    }

    @Override
    protected List<String> getTabCompleteFor(String arg) {
        List<String> strings = new ArrayList<>();
        for (CPlayer cPlayer : Core.getPlayerManager()) {
            String name = cPlayer.getName();
            if (name.toUpperCase().startsWith(arg.toUpperCase())) strings.add(name);
        }
        return strings;
    }

    @Override
    protected COfflinePlayer get(String target) {
        if (target.length() > 16) {
            if (!target.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) return null;
            return Core.getPlayerManager().getOfflinePlayerByUUID(UUID.fromString(target));
        }
        COfflinePlayer targetPlayer;
        List<CPlayer> possibleOnlinePlayers = Core.getPlayerManager().getCPlayerByStartOfName(target);
        if (possibleOnlinePlayers.size() != 1) {
            List<COfflinePlayer> offlinePlayers = Core.getPlayerManager().getOfflinePlayerByName(target);
            if (offlinePlayers.size() != 1) return null;
            targetPlayer = offlinePlayers.get(0);
        } else {
            targetPlayer = possibleOnlinePlayers.get(0);
        }
        return targetPlayer;
    }
}
