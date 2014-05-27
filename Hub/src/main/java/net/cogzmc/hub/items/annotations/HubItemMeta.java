package net.cogzmc.hub.items.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/21/2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HubItemMeta {
    /**
     * The key for the {@link net.cogzmc.hub.items.HubItem}, used for retrieving data from the config
     * @return the key
     */
    public String key();

    /**
     * Returns the permission required to use this {@link net.cogzmc.hub.items.HubItem}
     * @return the permission
     */
    public String permission() default "";

    /**
     * The slot to put the {@link net.cogzmc.hub.items.HubItem} in when added to the {@link org.bukkit.entity.Player}'s inventory.
     * If the slot is -1, the item will be added to the first open spot.
     */
    public int slot() default -1;
}