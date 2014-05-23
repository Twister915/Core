package net.cogzmc.hub.modules.spawn;

import net.cogzmc.hub.Hub;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class Spawn extends ModuleCommand {
    public Spawn() {
        super("spawn");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Hub.getInstance().getSpawnHandler().sendToSpawn(player.getBukkitPlayer());
    }
}
