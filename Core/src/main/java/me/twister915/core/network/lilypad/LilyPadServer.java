package me.twister915.core.network.lilypad;

import lilypad.client.connect.api.request.impl.RedirectRequest;
import lombok.Data;
import lombok.SneakyThrows;
import me.twister915.core.player.COfflinePlayer;
import me.twister915.core.player.CPlayer;
import me.twister915.core.network.NetworkServer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public final class LilyPadServer implements NetworkServer {
    private final String name;
    private final LilyPadNetworkManager networkManager;
    private Date lastPing = new Date();
    private Integer onlineCount = 0;
    private List<COfflinePlayer> players = new ArrayList<>();

    @Override
    @SneakyThrows
    public void sendPlayerToServer(CPlayer player) {
        networkManager.getConnect().request(new RedirectRequest(name, player.getName()));
    }
}
