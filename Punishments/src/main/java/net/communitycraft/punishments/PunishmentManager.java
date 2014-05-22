package net.communitycraft.punishments;

import net.communitycraft.core.player.CPlayer;
import net.communitycraft.core.player.CPlayerConnectionListener;
import net.communitycraft.core.player.CPlayerJoinException;

import java.net.InetAddress;

public class PunishmentManager implements CPlayerConnectionListener {
    @Override
    public void onPlayerJoin(CPlayer player, InetAddress address) throws CPlayerJoinException {

    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {

    }
}
