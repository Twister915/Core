package net.cogzmc.permissions.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPermissible;
import net.cogzmc.permissions.PermissionsReloadNetCommand;
import net.cogzmc.permissions.command.impl.nouns.*;
import net.cogzmc.permissions.command.impl.nouns.PlayerNoun;
import net.cogzmc.util.RandomUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public final class PermissionsCommand extends ModuleCommand {
    private Set<Noun<?>> nouns = new HashSet<>();

    public PermissionsCommand() {
        super("permissions");
        nouns.add(new GroupNoun());
        nouns.add(new PlayerNoun());
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) throw new ArgumentRequirementException("You have not specified enough arguments!");
        Noun n = getNounFor(args[0]);
        if (n == null) throw new ArgumentRequirementException("The target type you specified is invalid!");
        handleCommandUnspecific0(n, sender, args);
    }

    private Noun<?> getNounFor(String arg) {
        Noun<?> n = null;
        for (Noun<?> noun : nouns) {
            if (RandomUtils.contains(noun.getNames(), arg)) n = noun;
        }
        return n;
    }
    protected <T extends CPermissible> void handleCommandUnspecific0(Noun<T> n, CommandSender sender, String[] args) throws CommandException {
        Verb<T> verb = null;
        for (Verb<T> verb1 : n.getVerbs()) {
           if (RandomUtils.contains(verb1.getNames(), args[2])) verb = verb1;
        }
        if (verb == null) throw new ArgumentRequirementException("The verb you specified is not valid!");
        if (args.length-3 < verb.getRequiredArguments()) throw new ArgumentRequirementException("You have not specified enough arguments!");
        T target = n.get(args[1]);
        String[] strings = args.length <= 3 ? new String[]{} : Arrays.copyOfRange(args, 3, args.length);
        if (target == null && !verb.canAcceptNullTarget()) throw new ArgumentRequirementException("The target you specified is invalid!");
        else if (target == null) strings = new String[]{args[1]};
        verb.perform(sender, target, strings);
        Core.getPermissionsManager().save();
        broadcastNetCommand();
    }

    private void broadcastNetCommand() {
        if (Core.getNetworkManager() != null) Core.getNetworkManager().sendMassNetCommand(new PermissionsReloadNetCommand());
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //0 - noun
        //1 - noun autocomplete
        //2 - verb
        //3+ - not supported
        switch (args.length) {
            case 1:
                List<String> nounNames = new ArrayList<>();
                for (Noun<?> noun : nouns) {
                    for (String s : noun.getNames()) {
                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) nounNames.add(s);
                    }
                }
                return nounNames;
            case 2:
                Noun<?> nounFor = getNounFor(args[0]);
                if (nounFor == null) return Collections.emptyList();
            case 3:
                Noun<?> nounFor1 = getNounFor(args[0]);
                if (nounFor1 == null) return Collections.emptyList();
                List<String> strings = new ArrayList<>();
                for (Verb<? extends CPermissible> verb : nounFor1.getVerbs()) {
                    for (String s : verb.getNames()) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) strings.add(s);
                    }
                }
                return strings;
            default:
                return Collections.emptyList();
        }
    }
}
