package net.cogzmc.core.effect.npc.mobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.effect.npc.AbstractTameableMobNPC;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MobNPCHorse extends AbstractTameableMobNPC {
    public static enum ArmorType {
        NONE,
        IRON,
        GOLD,
        DIAMOND
    }

    @NonNull private Horse.Color color = Horse.Color.WHITE;
    @NonNull private Horse.Style style = Horse.Style.NONE;
    @NonNull private Horse.Variant variant = Horse.Variant.HORSE;
    @NonNull private ArmorType armorType = ArmorType.NONE;

    private boolean saddled;
    private boolean chest;
    private boolean bred;
    private boolean eating;
    private boolean rearing;
    private boolean mouthOpen;

    public MobNPCHorse(@NonNull Point location, World world, Set<CPlayer> observers, @NonNull String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.HORSE;
    }

    @Override
    protected Float getMaximumHealth() {
        return 30f;
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        //Bools
        int bools = 0;
        if (isTame()) bools |= 0x02;
        if (saddled) bools |= 0x04;
        if (chest) bools |= 0x08;
        if (bred) bools |= 0x10;
        if (eating) bools |= 0x20;
        if (rearing) bools |= 0x40;
        if (mouthOpen) bools |= 0x80;
        dataWatcher.setObject(16, bools);

        byte type = 0;
        switch (variant) {
            case HORSE:
                type = 0;
                break;
            case DONKEY:
                type = 1;
                break;
            case MULE:
                type = 2;
                break;
            case UNDEAD_HORSE:
                type = 3;
                break;
            case SKELETON_HORSE:
                type = 4;
                break;
        }
        dataWatcher.setObject(19, type);
        dataWatcher.setObject(20, color.ordinal() & 0xFF | style.ordinal() << 8);
        dataWatcher.setObject(21, getOwnerName());
        dataWatcher.setObject(22, armorType.ordinal());
    }
}
