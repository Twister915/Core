package net.cogzmc.punishments;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import net.cogzmc.punishments.command.LookupCommand;
import net.cogzmc.punishments.command.PermanentPunishCommand;
import net.cogzmc.punishments.command.TemporaryPunishCommand;
import net.cogzmc.punishments.command.UnPunishCommand;
import net.cogzmc.punishments.types.HumanFriendlyName;
import net.cogzmc.punishments.types.Punishment;
import net.cogzmc.punishments.types.TimedPunishment;
import net.cogzmc.punishments.types.impl.manager.*;
import net.cogzmc.punishments.types.impl.model.*;

import java.net.InetAddress;
import java.util.*;

@ModuleMeta(
        name = "Punishments",
        description = "Punishment manager!"
)
public final class Punishments extends ModularPlugin implements CPlayerConnectionListener {
    private final Map<Class<? extends Punishment>, PunishmentManager<?>> punishmentManagers = new LinkedHashMap<>();

    @Override
    protected void onModuleEnable() throws Exception {
        Core.getPlayerManager().registerCPlayerConnectionListener(this);
        //Notice the order, this is the specific order in which we want the join events passed.
        registerPunishmentManager(IPBan.class, new IPBanManager());
        registerPunishmentManager(Ban.class, new BanManager());
        registerPunishmentManager(TemporaryBan.class, new TemporaryBanManager());
        registerPunishmentManager(Mute.class, new MuteManager());
        registerPunishmentManager(TemporaryMute.class, new TemporaryMuteManager());
        registerPunishmentManager(Warning.class, new WarningManager());
        registerPunishmentManager(Kick.class, new KickManager());

        registerCommand(new LookupCommand());
    }

    public Collection<PunishmentManager<?>> getPunishmentManagers() {
        return punishmentManagers.values();
    }

    private <T extends Punishment> void registerPunishmentManager(Class<T> punishmentClass, PunishmentManager<T> punishmentManager) {
        punishmentManagers.put(punishmentClass, punishmentManager);
        if (TimedPunishment.class.isAssignableFrom(punishmentClass)) //noinspection unchecked
            registerCommand(new TemporaryPunishCommand<>((Class<? extends TimedPunishment>)punishmentClass));
        else registerCommand(new PermanentPunishCommand<>(punishmentClass));
        registerCommand(new UnPunishCommand<>(punishmentClass));
    }

    public <T extends Punishment> PunishmentManager<T> getPunishmentManager(Class<T> punishmentClass) {
        //noinspection unchecked
        return (PunishmentManager<T>) punishmentManagers.get(punishmentClass);
    }

    public static String getNameFor(Class<? extends Punishment> clazz) {
        return (clazz.isAnnotationPresent(HumanFriendlyName.class) ? clazz.getAnnotation(HumanFriendlyName.class).value() : clazz.getSimpleName()).toLowerCase();
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
        for (PunishmentManager<?> punishmentManager : punishmentManagers.values()) {
            punishmentManager.onPlayerLogin(player, address);
        }
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        for (PunishmentManager<?> punishmentManager : punishmentManagers.values()) {
            punishmentManager.onPlayerDisconnect(player);
        }
    }
}
