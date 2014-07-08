package net.cogzmc.core.test.tests.pathfinding;

import lombok.Data;
import net.cogzmc.core.effect.npc.pathfinding.PathfindingException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.test.TestModule;
import net.cogzmc.core.util.Point;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PathfindingTestCommand extends ModuleCommand {
    private final PathfindingTest test;
    public PathfindingTestCommand(PathfindingTest test) {
        super("pathfinding");
        this.test = test;
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        new TestRangeSelector(player, this, player.getBukkitPlayer().getWorld()).start();
    }

    private void handle(TestRangeSelector completed) {
        try {
            test.handleNewPath(new IllustratedPath(completed.getWorld(), completed.getStart(), completed.getEnd()));
        } catch (PathfindingException e) {
            completed.getPlayer().sendMessage(ChatColor.RED + "Pathfinding failed " + e.getMessage());
        }
    }

    @Data
    private static class TestRangeSelector implements Listener {
        private final CPlayer player;
        private final PathfindingTestCommand command;
        private final World world;
        private SelectionState state;

        private Point start;
        private Point end;

        public void start() {
            Bukkit.getServer().getPluginManager().registerEvents(this, TestModule.getInstance());
            player.sendMessage(ChatColor.GREEN + "Select a start to the path!");
            state = SelectionState.START;
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (!event.getPlayer().equals(player.getBukkitPlayer())) return;
            if (event.getAction() == Action.PHYSICAL) return;
            event.setCancelled(true);
            if (state == SelectionState.START) {
                start = Point.of(event.getClickedBlock());
                player.sendMessage(ChatColor.GREEN + "Select an end to the path!");
                state = SelectionState.END;
                return;
            }
            if (state == SelectionState.END) {
                end = Point.of(event.getClickedBlock());
                player.sendMessage(ChatColor.GREEN + "Setup the pathfinding test!");
                HandlerList.unregisterAll(this);
                command.handle(this);
            }
        }
    }

    private static enum SelectionState {
        START,
        END
    }
}
