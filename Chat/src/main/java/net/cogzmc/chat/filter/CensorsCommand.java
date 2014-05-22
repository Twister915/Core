package net.cogzmc.chat.filter;

import net.cogzmc.chat.ChatManager;
import net.communitycraft.core.modular.command.ArgumentRequirementException;
import net.communitycraft.core.modular.command.CommandException;
import net.communitycraft.core.modular.command.ModuleCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to manage {@link net.cogzmc.chat.filter.CensoredWord}s, by
 * removing, adding, or listing them.
 *
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/21/2014
 */
public class CensorsCommand extends ModuleCommand {
    public CensorsCommand() {
        super("censors");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) throw new ArgumentRequirementException("Must specify arguments");
        String cmd = args[0];
        Object[] censoredWords1 = ChatManager.getInstance().getCensoredWords();
        if (cmd.equalsIgnoreCase("list")) {
            sender.sendMessage(ChatManager.getInstance().getFormat("header-censorlist", false));
            int index = 0;
            for (Object o : censoredWords1) {
                index++;
                if (!(o instanceof String)) continue;
                String s = (String) o;
                sender.sendMessage(ChatManager.getInstance().getFormat("list-motdlist", false, new String[]{"<index>", String.valueOf(index)}, new String[]{"<motd>", s}));
            }
            return;
        }

        if (args.length < 2) throw new ArgumentRequirementException("Must specify arguments");
        List<String> strings = new ArrayList<>();
        for (Object o : censoredWords1) {
            if (o instanceof String) strings.add((String) o);
        }

        if (cmd.equalsIgnoreCase("remove")) {
            Integer toRemove = Integer.parseInt(args[1]);
            if (toRemove < 1 || toRemove > censoredWords1.length) {
                sender.sendMessage(ChatManager.getInstance().getFormat("index-out-of-range", false));
                return;
            }
            String s = strings.get(toRemove - 1);
            strings.remove(toRemove - 1);
            sender.sendMessage(ChatManager.getInstance().getFormat("removed-motd", false, new String[]{"<motd>", s}));
        } else if (cmd.equalsIgnoreCase("add")) {
            StringBuilder build = new StringBuilder();
            int index = 1;
            while (index < args.length) {
                build.append(args[index]).append(" ");
                index++;
            }
            String s = build.substring(0, build.length() - 1);
            strings.add(s);
            sender.sendMessage(ChatManager.getInstance().getFormat("added-motd", false, new String[]{"<motd>", s}));
        } else {
            throw new IllegalArgumentException("Invalid parameter");
        }
    }
}
