package net.cogzmc.coreessentials.server;

import net.cogzmc.coreessentials.CoreEssentials;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
final class ListCommand extends ModuleCommand {
    private final NetworkServer server;
    public ListCommand(NetworkServer server) {
        super("list");
        this.server = server;
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        List<String> playerNames = new ArrayList<>();
        for (UUID offlinePlayer : server.getPlayers()) {
            playerNames.add(Core.getOfflinePlayerByUUID(offlinePlayer).getDisplayName());
        }
        String playerList = Joiner.on(" ").skipNulls().join(playerNames);
        CoreEssentials coreEssentials = Core.getInstance().getModuleProvider(CoreEssentials.class);
        String message = coreEssentials.getFormat("server-list", new String[]{"<server>", server.getName()}, new String[]{"<players>", playerList});
        sender.sendMessage(message);
    }
}
