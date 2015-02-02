package net.cogzmc.core.effect.npc.mobs;

import net.cogzmc.core.effect.npc.AbstractAnimalNNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public final class MobNPCSheep extends AbstractAnimalNNPC {
    @NonNull private DyeColor color = DyeColor.WHITE;

    public MobNPCSheep(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.SHEEP;
    }

    @Override
    public Float getMaximumHealth() {
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
