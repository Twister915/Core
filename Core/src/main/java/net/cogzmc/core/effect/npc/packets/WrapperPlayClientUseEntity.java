package net.cogzmc.core.effect.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayClientUseEntity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.USE_ENTITY;

    public WrapperPlayClientUseEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientUseEntity(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the entity ID the player is interacting with.
     * @return The current Target
     */
    public int getTargetID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Retrieve the entity the player is interacting with.
     * @param world - the world this event occured in.
     * @return The target entity.
     */
    public Entity getTarget(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity the player is interacting with.
     * @param event - the current packet event.
     * @return The target entity.
     */
    public Entity getTarget(PacketEvent event) {
        return getTarget(event.getPlayer().getWorld());
    }

    /**
     * Set the entity ID the player is interacting with.
     * @param value - new value.
     */
    public void setTargetID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the use action.
     * @return The action.
     */
    public EnumWrappers.EntityUseAction getMouse() {
        return handle.getEntityUseActions().read(0);
    }

    /**
     * Set the use action.
     * @param value - new action.
     */
    public void setMouse(EnumWrappers.EntityUseAction value) {
        handle.getEntityUseActions().write(0, value);
    }
}
