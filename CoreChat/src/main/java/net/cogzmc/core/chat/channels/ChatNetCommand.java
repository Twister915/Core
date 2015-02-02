package net.cogzmc.core.chat.channels;

@AllArgsConstructor
@Data
public class ChatNetCommand implements NetCommand {
    public ChatNetCommand() {
    }
    private String message;
    private String channel;
    private String senderUUID;
}
