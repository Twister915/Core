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
    @NonNull private String ownerName = "Notch";

    public AbstractTameableMobNPC(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        byte value = 0;
        if (sitting) value |= 0x01;
        if (tame) value |= 0x04;
        dataWatcher.setObject(16, value);
        if (ownerName != null) dataWatcher.setObject(17, ownerName);
    }

    public void playHeartParticles() {
        playStatus(6);
    }

    public void playSmokeParticles() {
        playStatus(7);
    }

    public void playHeartParticles(Set<CPlayer> players) {
        playStatus(players, 6);
    }

    public void playSmokeParticles(Set<CPlayer> players) {
        playStatus(players, 7);
    }
}
