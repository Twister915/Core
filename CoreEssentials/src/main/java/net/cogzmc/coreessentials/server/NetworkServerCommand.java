package net.cogzmc.coreessentials.server;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.network.NetworkServer;
import net.cogzmc.core.player.CPlayer;

final class NetworkServerCommand extends ModuleCommand {
    private final NetworkServer networkServer;
    private final boolean canJoin;
    public NetworkServerCommand(NetworkServer networkServer) {
        super(networkServer.getName(), new ListCommand(networkServer));
        this.networkServer = networkServer;
        canJoin = !Core.getNetworkManager().getThisServer().equals(networkServer);
    }

    @Override
    protected void handleCommand(CPlayer commandSender, String[] args) throws CommandException {
        if (canJoin) networkServer.sendPlayerToServer(commandSender);
        else throw new ArgumentRequirementException("You cannot join this server!");
    }

    @Override
    protected boolean shouldGenerateHelpCommand() {
        return false;
    }
}
