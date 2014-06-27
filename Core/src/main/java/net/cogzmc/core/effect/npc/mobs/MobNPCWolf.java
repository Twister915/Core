package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractTameableMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCWolf extends AbstractTameableMobNPC {
    private boolean angry;
    private boolean begging;
    @NonNull private DyeColor collarColor = DyeColor.RED;

    public MobNPCWolf(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.WOLF;
    }

    @Override
    protected Float getMaximumHealth() {
        return isTame() ? 20f : 8f;
    }

    public void playShakeWater() {
        playStatus(9);
    }

    public void playShakeWater(Set<CPlayer> targets) {
        playStatus(targets, 9);
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        byte object = (byte) dataWatcher.getObject(16);
        if (angry) object |= 0x02;
        dataWatcher.setObject(16, object);
        dataWatcher.setObject(18, getHealth());
        if (begging) dataWatcher.setObject(19, ((byte) 1));
        else if (dataWatcher.getObject(19) != null) dataWatcher.removeObject(19);
        //noinspection deprecation
        if (isTame()) dataWatcher.setObject(20, collarColor.getWoolData());
    }
}
