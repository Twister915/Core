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
public final class MobNPCBat extends AbstractMobNPC {
    private boolean hanging;

    public MobNPCBat(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.BAT;
    }

    @Override
    protected Float getMaximumHealth() {
        return 6F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (hanging) dataWatcher.setObject(16, (byte)1);
        else if (dataWatcher.getObject(16) != null) dataWatcher.removeObject(16);
    }
}
