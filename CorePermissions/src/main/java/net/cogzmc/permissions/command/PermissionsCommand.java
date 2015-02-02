package net.cogzmc.permissions.command;

import net.cogzmc.permissions.PermissionsReloadNetCommand;
import net.cogzmc.permissions.command.impl.PermissionName;
import net.cogzmc.permissions.command.impl.nouns.*;

import java.util.*;

@CommandMeta(aliases = {"perm", "p"}, description = "Manages permissions for the server!")
public final class  PermissionsCommand extends ModuleCommand {
    private static final String PERMISSION = "core.permissions.manage";
    private Set<Noun<?>> nouns = new HashSet<>();

    public PermissionsCommand() {
        super("permissions", new PermReloadCommand());
        nouns.add(new GroupNoun());
        nouns.add(new PlayerNoun());
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!sender.hasPermission(PERMISSION)) throw new PermissionException("You do not have permission for this command!");
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
        if (!sender.hasPermission(getPermission(n, verb))) throw new PermissionException("You do not have permission for this command!");
        if (args.length-3 < verb.getRequiredArguments()) throw new ArgumentRequirementException("You have not specified enough arguments!");
        T target = n.get(args[1]);
        String[] strings = args.length <= 3 ? new String[]{} : Arrays.copyOfRange(args, 3, args.length);
        if (target == null && !verb.canAcceptNullTarget()) throw new ArgumentRequirementException("The target you specified is invalid!");
        else if (target == null) strings = new String[]{args[1]};
        verb.perform(sender, target, strings);
        Core.getPermissionsManager().save();
        Core.getPermissionsManager().reloadPermissions();
        if (Core.getNetworkManager() != null) Core.getNetworkManager().sendMassNetCommand(new PermissionsReloadNetCommand());
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(PERMISSION)) return Collections.emptyList();
        //0 - noun
        //1 - noun autocomplete
        //2 - verb
        //3+ - not supported
        switch (args.length) {
            case 1:
                List<String> nounNames = new ArrayList<>();
                for (Noun<?> noun : nouns) {
                    String s1 = args[0].toLowerCase();
                    for (String s : noun.getNames()) {
                        if (s.toLowerCase().startsWith(s1)) nounNames.add(s);
                    }
                }
                return nounNames;
            case 2:
                Noun<?> nounFor = getNounFor(args[0]);
                if (nounFor == null) return Collections.emptyList();
                return nounFor.getTabCompleteFor(args[1]);
            case 3:
                Noun<?> nounFor1 = getNounFor(args[0]);
                if (nounFor1 == null) return Collections.emptyList();
                List<String> strings = new ArrayList<>();
                for (Verb<? extends CPermissible> verb : nounFor1.getVerbs()) {
                    String s1 = args[2].toLowerCase();
                    for (String s : verb.getNames()) {
                        if (s.toLowerCase().startsWith(s1) && sender.hasPermission(getPermission(nounFor1, verb))) strings.add(s);
                    }
                }
                return strings;
            default:
                return Collections.emptyList();
        }
    }

    @Override
    protected boolean shouldGenerateHelpCommand() {
        return false;
    }

    private String getPermission(Noun noun, Verb verb) {
        String nounPerm = noun.getClass().getAnnotation(PermissionName.class).value();
        String verbPerm = verb.getClass().getAnnotation(PermissionName.class).value();
        if (nounPerm == null || verbPerm == null) throw new IllegalArgumentException("The arguments you supplied do not specify permission values!");
        return "core.permissions." + nounPerm + "." + verbPerm;
    }
}
