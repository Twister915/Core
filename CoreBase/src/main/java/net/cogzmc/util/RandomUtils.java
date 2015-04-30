package net.cogzmc.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.List;

public final class RandomUtils {
    public static <T> T safeCast(Object o, @SuppressWarnings("UnusedParameters") Class<T> type) {
        try {
            //noinspection unchecked
            return (T)o;
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    public static <T> T[] reverseArray(T[] array, Class<T> type) {
        @SuppressWarnings("unchecked") T[] ts = (T[])Array.newInstance(type, array.length);
        for (int x = 0, y = array.length-1; x < array.length; x++, y--) {
            ts[x] = array[y];
        }
        return ts;
    }

    public static <T> boolean contains(T[] ts, T t) {
        if (t == null || ts == null) return false;
        for (T t1 : ts) {
            if (t1 == null) continue;
            if (t1.equals(t)) return true;
        }
        return false;
    }

    public static <T> int indexOf(T[] ts, T t) {
        for (int x = 0; x < ts.length; x++) {
            if (ts[x].equals(t)) return x;
        }
        return -1;
    }

    public static <T> String formatList(List<? extends T> list, int max) {
        StringBuilder builder = new StringBuilder();
        int current = 0;
        for (T aValue : list) {
            if (current == max) break;
            builder.append(aValue).append(", ");
            current++;
        }
        if (list.size() > 0) {
            builder.deleteCharAt(builder.length() - 2);
        }
        return builder.toString();
    }

    public static boolean delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return false;
            for (File file1 : files) {
                if (!delete(file1)) return false;
            }
        }
        return file.delete();
    }

    public static void copy(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
        if (source.isDirectory()) {
            for (String s : source.list()) {
                copy(new File(source, s), new File(dest, s));
            }
        }
    }

    public static <T> boolean arrayContains(T[] ts, T t) {
        for (T t1 : ts) {
            if ((t1 == null || t == null) && t != t1) continue;
            if (t1 == t || t1.equals(t)) return true;
        }
        return false;
    }

    public static String formatSeconds(Integer seconds) {
        StringBuilder builder = new StringBuilder();
        int ofNext = seconds;
        for (TimeUnit unit : TimeUnit.values()) {
            int ofUnit;
            if (unit.perNext != -1) {
                ofUnit = ofNext % unit.perNext;
                ofNext = Math.floorDiv(ofNext, unit.perNext);
            }
            else {
                ofUnit = ofNext;
                ofNext = 0;
            }
            builder.insert(0, unit.shortName).insert(0, String.format("%02d", ofUnit));
            if (ofNext == 0) break;
        }
        return builder.toString();
    }

    private enum TimeUnit {
        SECONDS(60, 's'),
        MINUTES(60, 'm'),
        HOURS(24, 'h'),
        DAYS('d');

        private final int perNext;
        private final char shortName;

        TimeUnit(int i, char h) {
            perNext = i;
            shortName = h;
        }


        TimeUnit(char d) {
            perNext = -1;
            shortName = d;
        }
    }
}
