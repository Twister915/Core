package net.cogzmc.core.maps;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.World;

import java.io.File;
import java.util.UUID;

@Data
@Setter(AccessLevel.NONE)
public final class CMap {
    @NonNull private final CMapManager mapManager;
    @NonNull private final UUID mapId;
    private final World world;
    private final File zipFileHandle;
    @NonNull private final File worldFile;
    private boolean locked = false;
    private boolean loaded = false;

    public CMap(CMapManager manager, UUID mapId, World world) {
        mapManager = manager;
        this.mapId = mapId;
        this.world = world;
        this.zipFileHandle = null;
        this.worldFile = null;
    }

    public void load() {

    }

    public void unload() {

    }

    public void lock() {
        if (locked || !mapManager.canLock(this)) throw new IllegalStateException("The map is currently locked here or elsewhere!");
    }

    public void unlock() {
        if (!locked || !mapManager.canUnlock(this)) throw new IllegalStateException("The map is currently unlocked here or locked elsewhere!");
    }
}
