package net.cogzmc.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class LocationUtils {
    public static final String DIVIDER = ";";
    /**
     * Parses a string intended to represent a location. Used for databases, or config files. Really cool
     *
     * @param string The location string to parse
     * @return The location that the string represents
     */
    public static Location parseLocationString(String string) {
        String[] sep = string.split(LocationUtils.DIVIDER);
        if (sep.length < 6) {
            return null;
        }
        Location rv = new Location(Bukkit.getWorld(sep[0]), Double.valueOf(sep[1]), Double.valueOf(sep[2]), Double.valueOf(sep[3]));
        rv.setPitch(Float.parseFloat(sep[4]));
        rv.setYaw(Float.parseFloat(sep[5]));
        return rv;
    }

    /**
     * Encode a location into a string for storage
     *
     * @param location The location you intend to encode.
     * @return The string that represents the location.
     */
    public static String encodeLocationString(Location location) {
        return location.getWorld().getName() + LocationUtils.DIVIDER + location.getX() + LocationUtils.DIVIDER + location.getY() + LocationUtils.DIVIDER + location.getZ() + LocationUtils.DIVIDER + location.getPitch() + LocationUtils.DIVIDER + location.getYaw();
    }
}
