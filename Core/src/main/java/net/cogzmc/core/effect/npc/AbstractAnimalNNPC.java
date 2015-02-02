package net.cogzmc.core.effect.npc;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

public abstract class AbstractAnimalNNPC extends AbstractAgeableMobNPC {
    public AbstractAnimalNNPC(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    public void playMateAnimation() {
        playStatus(18);
    }

    public void playMateAnimation(Set<CPlayer> players) {
        playStatus(players, 18);
    }
}
