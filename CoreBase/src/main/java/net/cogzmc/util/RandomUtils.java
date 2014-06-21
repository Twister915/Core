package net.cogzmc.util;

import java.lang.reflect.Array;
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
        for (T t1 : ts) {
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
}
