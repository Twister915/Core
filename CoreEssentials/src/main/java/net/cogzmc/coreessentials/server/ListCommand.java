package net.cogzmc.coreessentials.server;

import com.google.common.base.Joiner;
import lombok.EqualsAndHashCode;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.network.NetworkServer;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.coreessentials.CoreEssentials;
import org.bukkit.command.CommandSender;

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
