package net.communitycraft.base.command;

import net.communitycraft.core.modular.command.ArgumentRequirementException;
import net.communitycraft.core.modular.command.CommandException;
import net.communitycraft.core.modular.command.ModuleCommand;
import net.communitycraft.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;

public class WorldCommand extends ModuleCommand {
    public WorldCommand() {
        super("world");
    }

    @Override
    public void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            StringBuilder builder = new StringBuilder();
            for (World w : Bukkit.getWorlds()) {
                builder.append(" ").append(ChatColor.AQUA).append(w.getName()).append(ChatColor.YELLOW).append(",");
            }
            String s = builder.toString();
            s = s.substring(0, s.length()-2);
            player.sendMessage(s);
            return;
        }
        World world = Bukkit.getWorld(args[0]);
        if (world == null) throw new ArgumentRequirementException("Invalid world supplied!");
        player.getBukkitPlayer().teleport(world.getSpawnLocation());
        player.playSoundForPlayer(Sound.ENDERMAN_TELEPORT);
    }
}
