package net.communitycraft.punishments;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;

import java.net.InetAddress;

public class PunishmentManager implements CPlayerConnectionListener {
    @Override
    public void onPlayerJoin(CPlayer player, InetAddress address) throws CPlayerJoinException {

    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {

    }
}
