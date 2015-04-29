package net.cogzmc.core.effect;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class ActionBar {
    public static void setActionBarFor(Player player, WrappedChatComponent text) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer chatPacket = protocolManager.createPacket(PacketType.Play.Server.CHAT);
        chatPacket.getChatComponents().write(0, text);
        chatPacket.getBytes().write(0, (byte) 2);
        try {
            protocolManager.sendServerPacket(player, chatPacket);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
