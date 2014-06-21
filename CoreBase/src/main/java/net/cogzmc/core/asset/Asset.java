package net.cogzmc.core.asset;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cogzmc.core.player.COfflinePlayer;

import java.util.Map;
import java.util.Random;

/**
 * This represents something a player can "have". An asset has metadata that you can modify through the exposed method {@link #getMetaVariables()}.
 *
 * A player can have many of the same type of {@link net.cogzmc.core.asset.Asset}, even ones with identical meta.
 */
@Data
@EqualsAndHashCode(of = {"metaVariables", "randomUID"})
public abstract class Asset {
    private final COfflinePlayer player;
    private final Map<String, ?> metaVariables;
    private static final Random RANDOM = new Random();
    private final Integer randomUID = RANDOM.nextInt();

    /**
     * This method will return any metadata associated with the asset.
     * @return A {@link java.util.Map} relating keys to values for information to be sored in the database. Keep these strictly to primitive objects any implementations of {@link java.util.List} or {@link java.util.Map} must only contain primitives at their lowest level.
     */
    public Map<String, ?> getMetaVariables() { return metaVariables; }
}
