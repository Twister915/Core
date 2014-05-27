package net.cogzmc.hub.modules.spawn;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandPermission;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.hub.Hub;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
@CommandPermission(
        value = "hub.setspawn"
)
public final class SetSpawn extends ModuleCommand {
    public SetSpawn() {
        super("setspawn");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Hub.getInstance().getSpawnHandler().setSpawn(player.getBukkitPlayer().getLocation());
        player.sendMessage(Hub.getInstance().getFormat("set-spawn"));
    }
}
