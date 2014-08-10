package net.cogzmc.core.network.heartbeat;

import java.util.List;
import java.util.UUID;

public interface HeartbeatHandler {
    void handleHeartbeatData(String server, Integer maxPlayers, List<UUID> uuids);
}
