package net.cogzmc.punishments.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.PermissionException;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.Punishment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

abstract class BasePunishCommand<T extends Punishment, M extends PunishmentManager<T>> extends TargetedCommand {
    protected final Class<T> clazz;
    protected final M punishmentManager;

    protected BasePunishCommand(String name, Class<T> clazz) {
        super(name);
        this.clazz = clazz;
        //noinspection unchecked
        this.punishmentManager = (M) Core.getModule(Punishments.class).getPunishmentManager(clazz);
    }

    @Override
    protected void handleCommand(CPlayer sender, String[] args) throws CommandException {

    }
}
