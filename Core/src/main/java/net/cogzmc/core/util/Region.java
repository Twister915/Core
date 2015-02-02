package net.cogzmc.core.util;

import lombok.Getter;
import lombok.Value;
import net.cogzmc.core.json.RegionSerializer;

import java.util.Iterator;

@Value
public final class Region implements Iterable<Point> {
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

    @Override
    public Iterator<Point> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<Point> {
        private Double x = min.getX();
        private Double y = min.getY();
        private Double z = min.getZ();

        @Override
        public boolean hasNext() {
            return !(x.equals(max.getX()) && y.equals(max.getY()) && z.equals(max.getZ()));
        }

        @Override
        public Point next() {
            Point of = Point.of(x, y, z);
            z++;
            if (z > max.getZ()) {
                z = min.getZ();
                y++;
                if (y > max.getY()) {
                    y = min.getY();
                    x++;
                }
            }
            return of;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("You cannot remove from a region!");
        }
    }
}
