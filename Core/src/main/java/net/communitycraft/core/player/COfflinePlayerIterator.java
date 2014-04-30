package net.communitycraft.core.player;

import lombok.Data;
import lombok.NonNull;

import java.util.Collection;
import java.util.Iterator;

@Data
public final class COfflinePlayerIterator implements Iterable<COfflinePlayer> {
    @NonNull private final Collection<COfflinePlayer> playersResolved;

    @Override
    public Iterator<COfflinePlayer> iterator() {
        return playersResolved.iterator();
    }
}
