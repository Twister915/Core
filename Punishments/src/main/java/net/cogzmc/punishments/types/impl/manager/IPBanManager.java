package net.cogzmc.punishments.types.impl.manager;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerJoinException;
import net.cogzmc.punishments.types.impl.model.IPBan;
import org.bson.types.ObjectId;

import java.net.InetAddress;
import java.util.Date;

public final class IPBanManager extends BaseMongoManager<IPBan> {
    public IPBanManager() {
        super(IPBan.class);
    }
    @Override
    IPBan createNewPunishment(ObjectId objectId, COfflinePlayer target, String reason, COfflinePlayer issuer, Date issued, Boolean active) {
        return new IPBan(objectId, target, reason, issuer, active, issued);
    }

    @Override
    boolean canConnect(CPlayer player, IPBan punishment) {
        return false;
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
        for (COfflinePlayer cOfflinePlayer : Core.getPlayerManager().getOfflinePlayersForIP(address)) {
            IPBan activePunishmentFor = getActivePunishmentFor(cOfflinePlayer);
            if (activePunishmentFor != null) throwJoinExceptionFor(activePunishmentFor);
        }
        super.onPlayerLogin(player, address);
    }
}
