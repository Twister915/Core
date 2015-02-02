package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public final class MobNPCEnderman extends AbstractMobNPC {
    private ItemStack carriedItemStack;
    private boolean screaming;

    public MobNPCEnderman(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.ENDERMAN;
    }

    @Override
    public Float getMaximumHealth() {
        return 40F;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        if (carriedItemStack != null) {
            dataWatcher.setObject(16, carriedItemStack.getTypeId());
            dataWatcher.setObject(17, carriedItemStack.getDurability());
        }
        if (screaming) dataWatcher.setObject(18, (byte)1);
        else if (dataWatcher.getObject(18) != null) dataWatcher.removeObject(18);
    }
}
