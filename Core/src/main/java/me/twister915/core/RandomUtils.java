package me.twister915.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public final class RandomUtils {
    public static void setDeclaredField(Object value, Object instance, String field) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = instance.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(instance, value);
    }

    public static <T> T getDeclaredField(Object o, String field, Class<T> type){
        try {
            return safeCast(o.getClass().getDeclaredField(field).get(o), type);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

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
}
