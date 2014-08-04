package net.cogzmc.core.util;

import lombok.Getter;
import lombok.Value;
import net.cogzmc.core.json.RegionSerializer;

@Value
public final class Region {
    @Getter private final static RegionSerializer serializer = new RegionSerializer();

    private final Point min;
    private final Point max;

    public Region(Point l1, Point l2) {
        Double maxX = Math.max(l1.getX(), l2.getX());
        Double maxY = Math.max(l1.getY(), l2.getY());
        Double maxZ = Math.max(l1.getZ(), l2.getZ());
        Double minX = Math.min(l1.getX(), l2.getX());
        Double minY = Math.min(l1.getY(), l2.getY());
        Double minZ = Math.min(l1.getZ(), l2.getZ());

        min = Point.of(minX, minY, minZ);
        max = Point.of(maxX, maxY, maxZ);
    }

    public boolean isWithin(Point p) {
        return  p.getX() <= max.getX() && p.getX() >= min.getX() &&
                p.getY() <= max.getY() && p.getY() >= min.getY() &&
                p.getZ() <= max.getZ() && p.getZ() >= min.getZ();
    }
}
