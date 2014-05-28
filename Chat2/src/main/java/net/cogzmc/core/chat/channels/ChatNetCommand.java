package net.cogzmc.core.chat.channels;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.cogzmc.core.network.NetCommand;

@AllArgsConstructor
@Data
public class ChatNetCommand implements NetCommand {
    public ChatNetCommand() {
    }
    private String message;
    private String channel;
    private String senderUUID;
}
