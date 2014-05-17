package net.communitycraft.core.network.lilypad;

import lilypad.client.connect.api.request.impl.MessageRequest;
import lilypad.client.connect.api.request.impl.RedirectRequest;
import lombok.Data;
import lombok.SneakyThrows;
import net.communitycraft.core.network.NetCommand;
import net.communitycraft.core.network.NetCommandField;
import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPlayer;
import net.communitycraft.core.network.NetworkServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
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

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    public void sendNetCommand(NetCommand command) {
        JSONObject object = new JSONObject(); //Create a holder for this NetCommand
        Class<? extends NetCommand> commandType = command.getClass(); //Command type
        object.put(LilyPadKeys.NET_COMMAND_CLASS_NAME, commandType.getName()); //Put the class name
        //Find the objects and values
        JSONObject arguments = new JSONObject();
        //Gets all the fields
        boolean allFields = commandType.isAnnotationPresent(NetCommandField.class);
        for (Field field : commandType.getDeclaredFields()) {
            if (!allFields && !field.isAnnotationPresent(NetCommandField.class)) continue;
            //And adds them when they have a NetCommandField annotation.
            arguments.put(field.getName(), field.get(command));
        }
        object.put(LilyPadKeys.NET_COMMAND_ARGUMENTS, arguments);
        object.put(LilyPadKeys.NET_COMMAND_TIME, new Date());
        networkManager.getConnect().request(new MessageRequest(name, LilyPadNetworkManager.NET_COMMAND_CHANNEL, object.toJSONString()));
    }
}
