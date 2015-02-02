package net.cogzmc.punishments.command;

import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.Punishment;

abstract class BasePunishCommand<T extends Punishment, M extends PunishmentManager<T>> extends TargetedCommand {
    protected final Class<T> clazz;
    protected final M punishmentManager;
    protected final String name;

    protected BasePunishCommand(String name, Class<T> clazz) {
        super(name);
        this.clazz = clazz;
        //noinspection unchecked
        this.punishmentManager = (M) Core.getModule(Punishments.class).getPunishmentManager(clazz);
        this.name = Punishments.getNameFor(clazz);
    }

    @Override
    protected void handleCommand(CPlayer sender, String[] args) throws CommandException {
        if (!sender.hasPermission("punish." + name)) throw new PermissionException("You do not have permission to " + name + " people!");
    }
}
