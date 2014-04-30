package net.communitycraft.core.modular.command;

import lombok.Getter;
import net.communitycraft.core.Core;
import net.communitycraft.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.String;
import java.util.*;

public abstract class ModuleCommand implements CommandExecutor, TabCompleter {
    private final Map<String, ModuleCommand> subCommands = new HashMap<>();
    @Getter private final String name;

    protected ModuleCommand(String name) {
        this.name = name;
    }

    public final boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //Handling commands can be done by the logic below, and all errors should be thrown using an exception.
        //If you wish to override the behavior of displaying that error to the player, it is discouraged to do that in
        //your command logic, and you are encouraged to use the provided method handleCommandException.
        try {
            //STEP ONE: Handle sub-commands

            ModuleCommand subCommand = null;
            //Check if we HAVE to use subcommands (a behavior this class provides)
            if (isUsingSubcommandsOnly()) {
                //Check if there are not enough args for there to be a sub command
                if (args.length < 1)
                    throw new ArgumentRequirementException("You must specify a subcommand for this command!", this, args, sender);
                //Also check if the sub command is valid by assigning and checking the value of the resolved sub command from the first argument.
                if ((subCommand = getSubCommandFor(args[0])) == null)
                    throw new ArgumentRequirementException("The sub command you have specified is invalid!", this, args, sender);
            }
            //By now we have validated that the sub command can be executed if it MUST, now lets see if we can execute it
            //In this case, if we must execute the sub command, this check will always past. In cases where it's an option, this check willa also pass.
            //That way, we can use this feature of sub commands without actually requiring it.
            if (subCommand != null) {
                String[] choppedArgs = Arrays.copyOfRange(args, 1, args.length - 1);
                subCommand.onCommand(sender, command, s, choppedArgs);
                return true;
            }

            //Now that we've made it past the sub commands, STEP TWO: actually handle the command and it's args.
            if (sender instanceof Player) {
                CPlayer player = Core.getPlayerManager().getCPlayerForPlayer((Player) sender);
                handleCommand(player, args);
            }
            if (sender instanceof ConsoleCommandSender) {
                handleCommand((ConsoleCommandSender)sender, args);
            }
            if (sender instanceof BlockCommandSender) {
                handleCommand((BlockCommandSender)sender, args);
            }
        } catch (CommandException ex) {
            handleCommandException(ex, args, sender);
        } catch (Exception e) {
            handleCommandException(new UnhandledCommandExceptionException(e, this, args, sender), args, sender);
        }
        return true;
    }

    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            ModuleCommand possibleHigherLevelSubCommand;
            if ((possibleHigherLevelSubCommand = getSubCommandFor(args[0])) != null)
                return possibleHigherLevelSubCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length-1));
        } else if (args.length == 1) {
            List<ModuleCommand> subCommandsForPartial = getSubCommandsForPartial(args[0]);
            if (subCommandsForPartial.size() != 0) {
                List<String> strings = new ArrayList<>();
                for (ModuleCommand moduleCommand : subCommandsForPartial) {
                    strings.add(moduleCommand.getName());
                }
                return strings;
            }
        }
        return handleTabComplete(sender, command, alias, args);
    }

    protected void handleCommandException(CommandException ex, String[] args, CommandSender sender) {
        sender.sendMessage(ChatColor.RED + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "!");
    }

    private ModuleCommand getSubCommandFor(String s) {
        if (subCommands.containsKey(s)) return subCommands.get(s);
        for (String s1 : subCommands.keySet()) {
            if (s1.equalsIgnoreCase(s)) return subCommands.get(s1);
        }
        return null;
    }

    private List<ModuleCommand> getSubCommandsForPartial(String s) {
        List<ModuleCommand> commands = new ArrayList<>();
        ModuleCommand subCommand;
        if ((subCommand = getSubCommandFor(s)) != null) {
            commands.add(subCommand);
            return commands;
        }
        String s2 = s.toUpperCase();
        for (String s1 : subCommands.keySet()) {
            if (s1.toUpperCase().startsWith(s2)) commands.add(subCommands.get(s1));
        }
        return commands;
    }

    protected void handleCommand(CPlayer player, String[] args) throws CommandException {}
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {}
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {}

    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {return Core.getInstance().onTabComplete(sender, command, alias, args);}

    protected boolean isUsingSubcommandsOnly() {return false;}
}
