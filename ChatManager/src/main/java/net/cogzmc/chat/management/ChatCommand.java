package net.cogzmc.chat.management;

import net.cogzmc.chat.ChatManager;
import net.cogzmc.chat.data.Chat;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import org.bukkit.command.CommandSender;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/21/2014
 */
public class ChatCommand extends ModuleCommand {
    public ChatCommand() {
        super("chat");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) throw new ArgumentRequirementException("You need to specify two arguments");
        Chat chat = ChatManager.getInstance().getChat();
        switch (args[0]) {
            case "mute":
                if (chat.isMuted()) {
                    sender.sendMessage(ChatManager.getInstance().getFormat("chat-is-muted"));
                } else {
                    sender.sendMessage(ChatManager.getInstance().getFormat("chat-mute-on"));
                    chat.setMuted(true);
                }
                break;
            case "unmute":
                if (chat.isMuted()) {
                    sender.sendMessage(ChatManager.getInstance().getFormat("chat-mute-off"));
                    chat.setMuted(false);
                } else {
                    sender.sendMessage(ChatManager.getInstance().getFormat("chat-not-muted"));
                    chat.setMuted(true);
                }
                break;
            default:
                throw new ArgumentRequirementException("Command parameter not found!");
        }
    }
}
