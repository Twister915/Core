package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class MobNPCSlime extends AbstractMobNPC {
    private int size = 1;

    public MobNPCSlime(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.SLIME;
    }

    @Override
    protected Float getMaximumHealth() {
        switch (size) {
            case 1:
                return 1f;
            case 2:
                return 4f;
            case 4:
                return 16f;
            default:
                return 16f;
        }
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        dataWatcher.setObject(16, (byte)size);
    }
}
