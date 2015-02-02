package net.cogzmc.core.effect.npc;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractAgeableMobNPC extends AbstractMobNPC {
    @Setter private boolean adult = true;

    public AbstractAgeableMobNPC(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        dataWatcher.setObject(12, adult ? 1 : -1); //Age (adult)
    }
}
