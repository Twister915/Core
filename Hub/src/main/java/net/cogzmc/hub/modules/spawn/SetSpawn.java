package net.cogzmc.hub.modules.spawn;

import net.cogzmc.hub.Hub;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.ChatColor;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class SetSpawn extends ModuleCommand {
    public SetSpawn() {
        super("setspawn");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Hub.getInstance().getSpawnHandler().setSpawn(player.getBukkitPlayer().getLocation());
        player.sendMessage(ChatColor.GREEN + "Set the spawn to your current location.");
    }
}
