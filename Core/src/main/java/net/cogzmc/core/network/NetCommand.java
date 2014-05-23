package net.cogzmc.core.network;

/**
 * The following should be implemented by anything that wishes to represent a {@link net.cogzmc.core.network.NetCommand}
 *
 * All fields with the annotation {@link net.cogzmc.core.network.NetCommandField} will be serialized and deserialized by the destination server.
 *
 * Given
 *
 * <pre>
 *     public final class MyNetCommand implements {@link net.cogzmc.core.network.NetCommand} {
 *         {@link net.cogzmc.core.network.NetCommandField} private String status = "WORKING";
 *         public void changeStatus(String newStatus) {
 *             this.status = newStatus;
 *         }
 *         public String getStatus() {
 *             return this.status;
 *         }
 *     }
 * </pre>
 *
 * You can send this {@link net.cogzmc.core.network.NetCommand} by doing the following
 *
 * <pre>
 *     MyNetCommand command = new MyNetCommand();
 *     command.changeStatus("TESTING");
 *     networkServerInstance.{@link net.cogzmc.core.network.NetworkServer#sendNetCommand(NetCommand)}(command);
 * </pre>
 *
 * And you can listen for this being sent to you by doing the following.
 *
 * <pre>
 *     public class MyNetCommandHandler implements {@link net.cogzmc.core.network.NetCommandHandler}<MyNetCommand> {
 *         public void {@link net.cogzmc.core.network.NetCommandHandler#handleNetCommand(NetworkServer, NetCommand)} {
 *             System.out.printLn("The status is " + command.getStatus());
 *         }
 *     }
 * </pre>
 *
 * And to register this listener.
 *
 * <pre>
 *     public void onModuleEnable() {
 *         Core.getNetworkManager().{@link net.cogzmc.core.network.NetworkManager#registerNetCommandHandler(NetCommandHandler, Class)}(new MyNetCommandHandler(), MyNetCommand.class);
 *     }
 * </pre>
 *
 * All implementations must have a <b>NO ARGS</b> constructor.
 */
public interface NetCommand {
}
