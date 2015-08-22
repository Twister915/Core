package net.cogzmc.core.effect.npc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;

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
        dataWatcher.setObject(12, (byte) (adult ? 1 : -1)); //Age (adult)
    }
}
