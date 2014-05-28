package net.cogzmc.coreessentials.server;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.network.NetworkServer;
import net.cogzmc.core.player.CPlayer;

final class NetworkServerCommand extends ModuleCommand {
    private final NetworkServer networkServer;
    public NetworkServerCommand(NetworkServer networkServer) {
        super(networkServer.getName(), new ListCommand(networkServer));
        this.networkServer = networkServer;
    }

    @Override
    protected void handleCommand(CPlayer commandSender, String[] args) throws CommandException {
        networkServer.sendPlayerToServer(commandSender);
    }
}
