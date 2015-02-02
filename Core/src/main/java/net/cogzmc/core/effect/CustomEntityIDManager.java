package net.cogzmc.core.effect;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public final class CustomEntityIDManager {
    private static int CURRENT_INDICE = 2000;

    public static int getNextId() {
        do {
            CURRENT_INDICE++;
        } while (isDuplicateId(CURRENT_INDICE));
        return CURRENT_INDICE;
    }

    private static boolean isDuplicateId(Integer id) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getEntityId() == id) return true;
            }
        }
        return false;
    }
}
