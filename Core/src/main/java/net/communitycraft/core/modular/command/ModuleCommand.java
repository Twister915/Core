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

    protected ModuleCommand(final String name, ModuleCommand... subCommands) {
        this.name = name;
        for (ModuleCommand subCommand : subCommands) {
            this.subCommands.put(subCommand.getName(), subCommand);
        }
        final Map<String, ModuleCommand> subCommandsLV = this.subCommands;
        this.subCommands.put("help", new ModuleCommand("help") {

            @Override
            protected void handleCommand(CPlayer player, String[] args) throws CommandException {
                sendHelp(player.getBukkitPlayer());
            }

            @Override
            protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
                sendHelp(commandSender);
            }

            @Override
            protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
                sendHelp(commandSender);
            }

            private void sendHelp(CommandSender sender) {
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, ModuleCommand> stringModuleCommandEntry : subCommandsLV.entrySet()) {
                     builder.append(stringModuleCommandEntry.getKey()).append("|");
                }
                String s = builder.toString();
                sender.sendMessage(ChatColor.AQUA + "/" + ChatColor.DARK_AQUA + name + ChatColor.YELLOW + " - [" + s.substring(0, s.length()-2) + "]");
            }
        });
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //Handling commands can be done by the logic below, and all errors should be thrown using an exception.
        //If you wish to override the behavior of displaying that error to the player, it is discouraged to do that in
        //your command logic, and you are encouraged to use the provided method handleCommandException.
        try {
            //STEP ONE: Handle sub-commands
            ModuleCommand subCommand = null;

            //Check if we HAVE to use sub-commands (a behavior this class provides)
            if (isUsingSubcommandsOnly()) {
                //Check if there are not enough args for there to be a sub command
                if (args.length < 1)
                    throw new ArgumentRequirementException("You must specify a subcommand for this command!");
                //Also check if the sub command is valid by assigning and checking the value of the resolved sub command from the first argument.
                if ((subCommand = getSubCommandFor(args[0])) == null)
                    throw new ArgumentRequirementException("The sub command you have specified is invalid!");
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
        } //STEP THREE: Check for any command exceptions (intended) and any exceptions thrown in general and dispatch a call for an unhandled error to the handler.
        catch (CommandException ex) {
            handleCommandException(ex, args, sender);
        } catch (Exception e) {
            handleCommandException(new UnhandledCommandExceptionException(e), args, sender);
        }
        //STEP FOUR: Tell Bukkit we're done!
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //Step one, check if we have to go a level deeper in the sub command system:
        if (args.length > 1) {
            //If so, check if there's an actual match for the sub-command to delegate to.
            ModuleCommand possibleHigherLevelSubCommand;
            if ((possibleHigherLevelSubCommand = getSubCommandFor(args[0])) != null)
                return possibleHigherLevelSubCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length-1));
            //NOW THINK. If there's not one, you'll reach this line, and exit this block of the if statement. The next statement is an else if, so it will skip that
            //And go to the very bottom "handleTabComplete."
        } else if (args.length == 1) { //So if we have exactly one argument, let's try and complete the sub-command for that argument
            //Grab some sub commands from the method we defined for this purpose
            List<ModuleCommand> subCommandsForPartial = getSubCommandsForPartial(args[0]);
            //And if we found some
            if (subCommandsForPartial.size() != 0) {
                //Get the command names
                List<String> strings = new ArrayList<>();
                for (ModuleCommand moduleCommand : subCommandsForPartial) {
                    strings.add(moduleCommand.getName());
                }
                //And return them
                return strings;
            }
            //Otherwise, head to the delegated call at the bottom.
        }
        return handleTabComplete(sender, command, alias, args);
    }

    protected void handleCommandException(CommandException ex, String[] args, CommandSender sender) {
        sender.sendMessage(ChatColor.RED + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "!");
    }

    public final ModuleCommand getSubCommandFor(String s) {
        //If we have an exact match, case and all, don't waste the CPU cycles on the lower for loop.
        if (subCommands.containsKey(s)) return subCommands.get(s);
        //Otherwise, loop through the sub-commands and do a case insensitive check.
        for (String s1 : subCommands.keySet()) {
            if (s1.equalsIgnoreCase(s)) return subCommands.get(s1);
        }
        //And we didn't find anything, so let's return nothing.
        return null;
    }

    public final List<ModuleCommand> getSubCommandsForPartial(String s) {
        List<ModuleCommand> commands = new ArrayList<>(); //Create a place to hold our possible commands
        ModuleCommand subCommand;
        if ((subCommand = getSubCommandFor(s)) != null) { //Check if we can get an exact sub-command
            commands.add(subCommand);
            return commands; //exact sub-command is all we need.
        }
        String s2 = s.toUpperCase(); //Get the case-insensitive comparator.
        for (String s1 : subCommands.keySet()) {
            if (s1.toUpperCase().startsWith(s2)) commands.add(subCommands.get(s1)); //We found one that starts with the argument.
        }
        return commands;
    }

    protected void handleCommand(CPlayer player, String[] args) throws CommandException {}
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {}
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {}

    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {return Core.getInstance().onTabComplete(sender, command, alias, args);}

    protected boolean isUsingSubcommandsOnly() {return false;}
}
