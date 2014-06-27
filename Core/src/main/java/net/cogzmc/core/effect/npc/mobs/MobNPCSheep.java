package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractAgeableMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public final class MobNPCSheep extends AbstractAgeableMobNPC {
    @NonNull private DyeColor color = DyeColor.WHITE;

    public MobNPCSheep(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.SHEEP;
    }

    @Override
    protected Float getMaximumHealth() {
        return 8F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        //noinspection deprecation
        dataWatcher.setObject(16, color.getData());
    }

    public void playGrassEat() {
        playStatus(10);
    }

    public void playGrassEat(Set<CPlayer> players) {
        playStatus(players, 10);
    }
}
