package net.cogzmc.core.effect;

import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class Animations {
    public static void redFlashEntity(LivingEntity entity) {
        sendAnimationFor(entity, WrapperPlayServerAnimation.Animations.DAMAGE_ANIMATION);
    }

    public static void crouchPlayer(Player player) {
        sendAnimationFor(player, WrapperPlayServerAnimation.Animations.CROUCH);
    }

    public static void uncrouchPlayer(Player player) {
        sendAnimationFor(player, WrapperPlayServerAnimation.Animations.UNCROUCH);
    }

    private static void sendAnimationFor(LivingEntity entity, Integer animationType) {
        WrapperPlayServerAnimation packet = new WrapperPlayServerAnimation();
        packet.setEntityID(entity.getEntityId());
        packet.setAnimation(animationType);
        for (Player player : ProtocolLibrary.getProtocolManager().getEntityTrackers(entity)) {
            packet.sendPacket(player);
        }
    }
}
