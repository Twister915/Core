package net.cogzmc.core.maps;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.UUID;

@Data
@Setter(AccessLevel.NONE)
public final class CMap {
    @NonNull private final UUID mapId;
    private World world;
    private File zipFileHandle;
    private File worldFile;
    private boolean loaded = false;
    private String name;
    @Setter(AccessLevel.PACKAGE) private ObjectId gridFSId = null;
    @Setter(AccessLevel.PACKAGE) private ObjectId mongoId = null;

    public CMap(UUID mapId, World world) {
        this.mapId = mapId;
        this.world = world;
        this.zipFileHandle = null;
        this.worldFile = world.getWorldFolder();
        this.loaded = true;
    }

    public CMap(UUID mapId, File zipFileHandle) {
        this.mapId = mapId;
        this.zipFileHandle = zipFileHandle;
        this.worldFile = null;
        this.world = null;
        this.loaded = false;
    }

    public void load(String name) {
        if (loaded) throw new IllegalStateException("You cannot load a map that is already loaded!");
        File file1;
        try {
            ZipFile file = new ZipFile(zipFileHandle);
            file1 = new File(Bukkit.getWorldContainer(), name);
            file1.mkdir();
            file.extractAll(file1.getPath());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
        World world1 = Bukkit.createWorld(WorldCreator.name(name).environment(World.Environment.NORMAL));
        if (world1 == null) throw new RuntimeException("Could not load world!");
        this.worldFile = world1.getWorldFolder();
        this.world = world1;
        this.loaded = true;
        this.name = name;
    }

    public void unload() {
        Bukkit.unloadWorld(world, false);
        delete(worldFile);
    }

    private static void delete(@NonNull File file) {
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                delete(file1);
            }
        } else {
            file.delete();
        }
    }
}
