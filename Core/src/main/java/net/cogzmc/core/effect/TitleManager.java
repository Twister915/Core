package net.cogzmc.core.effect;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.cogzmc.core.Core;
import net.cogzmc.core.PlayerTargets;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.VersionUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class TitleManager {
    public enum TitleAction {
        TITLE(0),
        SUBTITLE(1),
        TIMES(2),
        CLEAR(3),
        RESET(4);

        private final int id;

        TitleAction(int id) {
            this.id = id;
        }
    }

    public void displayTitle(PlayerTargets player, WrappedChatComponent title, Integer fadeIn, Integer stay, Integer fadeOut) {
        sendPacket(player, TitleAction.TITLE, title, null, null, null);
        sendPacket(player, TitleAction.TIMES, null, fadeIn, stay, fadeOut);
    }

    public void displaySubtitle(PlayerTargets player, WrappedChatComponent subtitle, Integer fadeIn, Integer stay, Integer fadeOut) {
        sendPacket(player, TitleAction.SUBTITLE, subtitle, null, null, null);
        sendPacket(player, TitleAction.TIMES, null, fadeIn, stay, fadeOut);
    }

    public void clearTitle(PlayerTargets player) {
        sendPacket(player, TitleAction.CLEAR, null, null, null, null);
    }

    private void sendPacket(PlayerTargets players, TitleAction action, WrappedChatComponent text, Integer in, Integer stay, Integer out) {
        if (!Core.getInstance().isHasProtocolLib()) throw new IllegalStateException("You must be using ProtocolLib!");
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TITLE);
        try {
            //hacky way to do this
            Class<?> aClass = Class.forName("org.spigotmc.ProtocolInjector$PacketTitle$Action"); //1.8 class
            if (aClass == null) {
                //1.8+ server
                packet.getTitleActions().write(0, EnumWrappers.TitleAction.values()[action.id]);
            }
            else {
                //1.7 server
                StructureModifier specificModifier = packet.getSpecificModifier(aClass);
                Object[] values = (Object[]) aClass.getMethod("values").invoke(null);
                //noinspection unchecked
                specificModifier.write(0, values[action.id]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (text != null) packet.getChatComponents().write(0, text);
        StructureModifier<Integer> integers = packet.getIntegers();
        if (in != null) integers.write(0, in*20);
        if (stay != null) integers.write(1, stay*20);
        if (out != null) integers.write(2, out*20);
        players.forEach((player) -> {
            if (!shouldSend(player)) return;
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player.getBukkitPlayer(), packet, true);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean shouldSend(CPlayer player) {
        return VersionUtil.is18(player.getBukkitPlayer());
    }
}
