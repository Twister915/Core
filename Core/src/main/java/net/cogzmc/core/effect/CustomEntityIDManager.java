package net.cogzmc.core.effect;

public final class CustomEntityIDManager {
    private static int CURRENT_INDICE = 2000;

    public static int getNextId() {
        CURRENT_INDICE++;
        return CURRENT_INDICE;
    }
}
