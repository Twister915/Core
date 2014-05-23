package net.cogzmc.core.entities;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
//TODO finish this
public abstract class FakeEntity {
    private final EntityType type;
    private final CPlayer player;

    private final List<EntityObserver<?>> observers = new ArrayList<>();
    private final Integer id;
    private final WrappedDataWatcher dataWatcher;

    private Float health = 1f;
    private String customEntityName = null;
    private List<PotionEffect> potionEffects = new ArrayList<>();
    private Location currentLocation;

    protected FakeEntity(EntityType type, CPlayer player, Location startingLocation) {
        this.type = type;
        this.player = player;
        this.currentLocation = startingLocation;

        Entity entity = startingLocation.getWorld().spawnEntity(startingLocation, type);
        this.dataWatcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
        this.id = entity.getEntityId();
        entity.remove();
    }

    public final void teleport(Location l) {
        this.currentLocation = l;
        updateEntityState();
    }

    public final void setDisplayName(String name) {
        this.customEntityName = name;
        updateEntityState();
    }

    public final <T extends FakeEntity> void addObserver(EntityObserver<T> observer, Class<T> fakeEntityType) {
        if (!fakeEntityType.equals(getClass())) throw new IllegalArgumentException("You must add an observer of the right type!");
        if (!observers.contains(observer)) observers.add(observer);
    }

    private void updateEntityState() {

    }

    protected abstract int getHealthInteger();
}
