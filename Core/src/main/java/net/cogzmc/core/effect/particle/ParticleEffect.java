package net.cogzmc.core.effect.particle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Data;
import lombok.NonNull;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Used to represent a single particle being emitted at a location, and can be sent to many players or a single player
 * using the {@link #(Long)} or {@link #(org.bukkit.entity.Player)} methods provided by this
 * class.
 */
@Data
public final class ParticleEffect {
    /**
     * The type of particle to be sent.
     */
    @NonNull private final ParticleEffectType type;

    /**
     * This will be multiplied by a random number between 1.0 and 0.0. This is basically setting a limit for how far
     * around randomly on the X axis we can be with this particle effect.
     */
    @NonNull private Float xSpread = 0f;
    /**
     * This will be multiplied by a random number between 1.0 and 0.0. This is basically setting a limit for how far
     * around randomly on the Y axis we can be with this particle effect.
     */
    @NonNull private Float ySpread = 0f;
    /**
     * This will be multiplied by a random number between 1.0 and 0.0. This is basically setting a limit for how far
     * around randomly on the Y axis we can be with this particle effect.
     */
    @NonNull private Float zSpread = 0f;
    /**
     * Number of particles to be displayed.
     */
    @NonNull private Integer amount = 1;
    /**
     * Speed is how quickly they dissipate.
     */
    private Float speed;

    /**
     * Allows you to set a custom ID for the particle instead of using the {@link #type} in the constructor.
     */
    private String customParticle = null;

    public ParticleEffect(String s) {
        this.type = null;
        this.customParticle = s;
    }

    public ParticleEffect(@NonNull ParticleEffectType type) {
        this.type = type;
    }

    private PacketContainer getPacket(Location location) {
        if (!Core.getInstance().isHasProtocolLib()) throw new IllegalStateException("You must be using ProtocolLib!");
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        packet.getStrings().write(0, type.toString());
        StructureModifier<Float> floatStructure = packet.getFloat();
        floatStructure.write(0, ((float) location.getX()));//location
        floatStructure.write(1, ((float) location.getY()));
        floatStructure.write(2, ((float) location.getZ()));
        floatStructure.write(3, xSpread);//offset
        floatStructure.write(4, ySpread);
        floatStructure.write(5, zSpread);
        packet.getIntegers().write(0, amount);
        return packet;
    }

    /**
     * Sends the particle effect you have created to the specified online player.
     * @param player The player to send the packet to.
     */
    public void emitToPlayer(@NonNull CPlayer player, Location location) throws InvocationTargetException {
        PacketContainer packet = getPacket(location);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player.getBukkitPlayer(), packet);
    }

    public void emitToPlayers(@NonNull Iterable<CPlayer> players, Location location) throws InvocationTargetException {
        PacketContainer packet = getPacket(location);
        for (CPlayer player : players) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getBukkitPlayer(), packet);
        }
    }

    /**
     * Emits a particle effect to all players within a defined radius.
     * @param radius The radius to send the particle within.
     */
    public void emitGlobally(Long radius, Location location) throws InvocationTargetException {
        double distanceSquared = Math.pow(radius, 2); //Distance squared is faster than doing sqrt always.
        PacketContainer packet = getPacket(location);
        for (Player player : location.getWorld().getPlayers()) {
            //Determines if the distance from where our particle will be is less than our radius, and marks for sending if so
            if (player.getLocation().distanceSquared(location) <= distanceSquared) ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
    }

    public ParticleEffect withSpread(@NonNull Float spread) {
        this.xSpread = spread;
        this.ySpread = spread;
        this.zSpread = spread;
        return this;
    }
}
