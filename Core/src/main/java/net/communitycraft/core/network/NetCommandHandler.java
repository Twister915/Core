package net.communitycraft.core.network;

/**
 * Implement this to handle a {@link net.communitycraft.core.network.NetCommand} targeted at your server.
 * @param <NetCommandType> The {@link net.communitycraft.core.network.NetCommand} type to respond to.
 */
public interface NetCommandHandler<NetCommandType extends NetCommand> {
    /**
     * Handler for the incoming {@link net.communitycraft.core.network.NetCommand}. Will be called with an instance of the server that sent it and the net command that was sent.
     * @param sender The {@link net.communitycraft.core.network.NetworkServer} that sent the command to this server.
     * @param netCommand The instance of the {@link net.communitycraft.core.network.NetCommand} that was sent to this server.
     */
    void handleNetCommand(NetworkServer sender, NetCommandType netCommand);
}
