package net.cogzmc.gameapi.model.arena;

import java.util.UUID;

public final class ArenaUtils {
    public static String createRandomWorldName() {
        return UUID.randomUUID().toString().replaceAll("\\-", "");
    }
}
