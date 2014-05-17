package net.communitycraft.core.network.lilypad;

import lilypad.client.connect.api.request.impl.RedirectRequest;
import lombok.Data;
import lombok.SneakyThrows;
import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPlayer;
import net.communitycraft.core.network.NetworkServer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public final class LilyPadServer implements NetworkServer {
    private final String name;
    private final LilyPadNetworkManager networkManager;
    private Date lastPing = new Date();
    private List<COfflinePlayer> players = new ArrayList<>();

    @Override
    @SneakyThrows
    public void sendPlayerToServer(CPlayer player) {
        networkManager.getConnect().request(new RedirectRequest(name, player.getName()));
    }

    @Override
    public Integer getOnlineCount() {
        return players.size();
    }
}
