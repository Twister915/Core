package net.cogzmc.core.effect.particle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Data;
import net.cogzmc.core.PlayerTargets;
import net.cogzmc.core.util.Point;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@Data
public final class ProtocolLibParticleEffectEmitter {
    private int amount = 1;
    private String name;
    private Point location, offset;
    private boolean longDistance = false;
    private float particleData = 0;
    private int[] data;

    public void emit(PlayerTargets targets) {
        if (location == null || offset == null || name == null) throw new IllegalStateException("You have not defined all the required values for a particle!");
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        packet.getStrings().write(0, name);
        StructureModifier<Float> floatStructure = packet.getFloat();
        floatStructure.write(0, location.getX().floatValue());//location
        floatStructure.write(1, location.getY().floatValue());
        floatStructure.write(2, location.getZ().floatValue());
        floatStructure.write(3, offset.getX().floatValue());//offset
        floatStructure.write(4, offset.getY().floatValue());
        floatStructure.write(5, offset.getZ().floatValue());
        if (particleData != 0) floatStructure.write(6, particleData);//?!?!?
        packet.getIntegers().write(0, amount);
        if (data != null && data.length != 0) packet.getIntegerArrays().write(0, data);
        for (Player target : targets) {
            try {
                protocolManager.sendServerPacket(target, packet, true);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
