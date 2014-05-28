package net.cogzmc.coreessentials.server;

import com.google.common.collect.ImmutableList;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.network.NetworkServer;
import net.cogzmc.core.network.NetworkServerDiscoverObserver;

public final class ServerCommand extends ModuleCommand implements NetworkServerDiscoverObserver {
    public ServerCommand() {
        super("server");
        Core.getNetworkManager().registerNetworkServerDiscoverObserver(this);
    }
    public void updateSubCommands() {
        ImmutableList<ModuleCommand> subCommands = getSubCommands();
        unregisterSubCommand(subCommands.toArray(new ModuleCommand[subCommands.size()]));
        for (NetworkServer networkServer : Core.getNetworkManager().getServers()) {
            if (!networkServer.equals(Core.getNetworkManager().getThisServer()))
                registerSubCommand(new NetworkServerCommand(networkServer));
        }
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }

    @Override
    public void onNetworkServerDiscover(NetworkServer server) {
        updateSubCommands();
    }

    @Override
    public void onNetworkServerRemove(NetworkServer remove) {
        updateSubCommands();
    }
}
