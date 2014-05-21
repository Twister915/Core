package net.communitycraft.core.network.lilypad;

import lilypad.client.connect.api.request.impl.MessageRequest;
import lilypad.client.connect.api.request.impl.RedirectRequest;
import lombok.Data;
import lombok.SneakyThrows;
import net.communitycraft.core.network.NetCommand;
import net.communitycraft.core.network.NetworkServer;
import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPlayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.communitycraft.core.network.lilypad.LilyPadNetworkManager.encodeNetCommand;

@Data
final class LilyPadServer implements NetworkServer {
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

    @Override
    @SneakyThrows
    public void sendNetCommand(NetCommand command) {
        networkManager.getConnect().request(new MessageRequest(name, LilyPadNetworkManager.NET_COMMAND_CHANNEL, encodeNetCommand(command).toJSONString()));
    }
}
