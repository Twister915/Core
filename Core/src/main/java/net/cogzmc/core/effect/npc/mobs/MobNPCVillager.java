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

import static org.bukkit.entity.Villager.Profession;


@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCVillager extends AbstractMobNPC {
    private Profession profession;

    public MobNPCVillager(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        this(location, world, observers, title, null);
    }

    public MobNPCVillager(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title, Profession profession) {
        super(location, world, observers, title);
        this.profession = profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
        updateDataWatcher();
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Float getMaximumHealth() {
        return 20F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        if (profession != null) dataWatcher.setObject(16, profession.getId()); //Profession
        else if (dataWatcher.getObject(16) != null) dataWatcher.removeObject(16);
    }

    public void playAngryVillagerAnimation() {
        playStatus(13);
    }

    public void playAngryVillagerAnimation(Set<CPlayer> players) {
        playStatus(players, 13);
    }

    public void playHappyVillagerAnimation() {
        playStatus(14);
    }

    public void playHappyVillagerAnimation(Set<CPlayer> players) {
        playStatus(players, 14);
    }

}
