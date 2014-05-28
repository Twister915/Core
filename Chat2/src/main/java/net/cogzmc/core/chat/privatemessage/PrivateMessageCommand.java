package net.cogzmc.core.chat.privatemessage;

import com.google.common.base.Joiner;
import net.cogzmc.core.Core;
import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;

import java.util.Arrays;
import java.util.List;

@CommandMeta(aliases = {"pm", "whisper", "w", "tell", "t", "message"})
public final class PrivateMessageCommand extends ModuleCommand {
    public PrivateMessageCommand() {
        super("msg");
    }

    @Override
    protected void handleCommand(CPlayer commandSender, String[] args) throws CommandException {
        if (args.length < 2) throw new ArgumentRequirementException("You must specify a target and a message!");
        List<CPlayer> cPlayerByStartOfName = Core.getPlayerManager().getCPlayerByStartOfName(args[0]);
        if (cPlayerByStartOfName.size() != 1) throw new ArgumentRequirementException("The target you specified is " +
                (cPlayerByStartOfName.size() == 0 ? "not on the server" : "not specific enough") + "!");
        CPlayer target = cPlayerByStartOfName.get(0);
        String message = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
        target.sendMessage(CoreChat.getInstance().getFormat("private-message",
                new String[]{"<person>", commandSender.getDisplayName()},
                new String[]{"<direction>", "FROM"},
                new String[]{"<message>", message})
        );
        commandSender.sendMessage(CoreChat.getInstance().getFormat("private-message",
                new String[]{"<person>", target.getDisplayName()},
                new String[]{"<direction>", "TO"},
                new String[]{"<message>", message})
        );
    }
}
