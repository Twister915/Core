package net.cogzmc.core.util;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.entity.Player;

public final class VersionUtil {
    public static boolean is18(Player player) {
        return ProtocolLibrary.getProtocolManager().getProtocolVersion(player) >= 47;
    }
}
