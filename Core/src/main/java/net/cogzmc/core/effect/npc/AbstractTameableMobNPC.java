package net.cogzmc.core.effect.npc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractTameableMobNPC extends AbstractAgeableMobNPC {
    private boolean tame = false;
    private boolean sitting = false;
    private String ownerName = "Notch";

    public AbstractTameableMobNPC(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        byte mask = 0;
        mask |= (tame?1:0)<<0x01;
        mask |= (sitting?1:0)<<0x04;
        dataWatcher.setObject(16, mask);
        if (ownerName != null) dataWatcher.setObject(17, ownerName);
    }
}
