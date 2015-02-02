package net.cogzmc.core.effect.npc;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.lang.reflect.Constructor;
import java.util.Set;

public final class NPCFactory {
    public static <T extends AbstractMobNPC> T createNPC(@NonNull Class<T> type, @NonNull Point location, @NonNull Set<CPlayer> viewers, @NonNull String title) {
        try {
            Constructor<T> declaredConstructor = type.getDeclaredConstructor(Point.class, Set.class, String.class);
            return declaredConstructor.newInstance(location, viewers, title);
        } catch (Exception e) {
            throw new RuntimeException("Could not create mob!", e);
        }
    }
}
