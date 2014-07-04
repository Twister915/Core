package net.cogzmc.permissions.command.impl.verbs;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.permissions.command.Verb;
import org.bukkit.command.CommandSender;

@Getter
public final class GroupCreateVerb extends Verb<CGroup> {
    private final String[] names = new String[]{"create"};
    private final Integer requiredArguments = 1;

    @Override
    protected void perform(CommandSender sender, CGroup target, String[] args) throws CommandException {
        if (target != null) throw new ArgumentRequirementException("The group you are trying to create already exists!");
        Core.getPermissionsManager().createNewGroup(args[0]);
    }

    @Override
    protected boolean canAcceptNullTarget() {
        return true;
    }
}
