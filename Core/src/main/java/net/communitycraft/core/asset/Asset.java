package net.communitycraft.core.asset;

import lombok.Data;
import net.communitycraft.core.player.COfflinePlayer;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Asset {
    private final COfflinePlayer player;
    private final Map<String, ?> metaVariables;

    public Map<String, ?> getMetaVariables() { return new HashMap<>();}

    /* hooks */

}
