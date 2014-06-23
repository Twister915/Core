package net.cogzmc.core.effect.particle;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Used to represent a single particle being emitted at a location, and can be sent to many players or a single player
 * using the {@link #emitGlobally(Long)} or {@link #emitToPlayer(org.bukkit.entity.Player)} methods provided by this
 * class.
 */
@Data
public final class ParticleEffect {
    /**
     * The type of particle to be sent.
     */
    @NonNull private final ParticleEffectType type;
    /**
     * Where the particle should be centered.
     */
    @NonNull private final Location location;

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

    private WrapperPlayServerWorldParticles getPacket() {
        WrapperPlayServerWorldParticles packet = new WrapperPlayServerWorldParticles();
        packet.setLocation(location);
        packet.setOffsetX(xSpread);
        packet.setOffsetY(ySpread);
        packet.setOffsetZ(zSpread);
        packet.setNumberOfParticles(amount);
        packet.setParticleName(customParticle != null ? customParticle : type.toString());
        if (speed != null) packet.setParticleSpeed(speed);
        return packet;
    }

    /**
     * Sends the particle effect you have created to the specified online player.
     * @param player The player to send the packet to.
     */
    public void emitToPlayer(@NonNull Player player) {
        getPacket().sendPacket(player);
    }

    /**
     * Emits a particle effect to all players within a defined radius.
     * @param radius The radius to send the particle within.
     */
    public void emitGlobally(Long radius) {
        double distanceSquared = Math.pow(radius, 2); //Distance squared is faster than doing sqrt always.
        WrapperPlayServerWorldParticles packet = getPacket();
        for (Player player : location.getWorld().getPlayers()) {
            //Determines if the distance from where our particle will be is less than our radius, and marks for sending if so
            if (player.getLocation().distanceSquared(location) <= distanceSquared) packet.sendPacket(player);
        }
    }

    public ParticleEffect withSpread(@NonNull Float spread) {
        this.xSpread = spread;
        this.ySpread = spread;
        this.zSpread = spread;
        return this;
    }
}
