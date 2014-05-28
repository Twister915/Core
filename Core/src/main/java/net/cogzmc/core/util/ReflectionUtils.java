package net.cogzmc.core.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtils {
    private static String minecraftVersion;
    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        minecraftVersion = packageName.substring(packageName.lastIndexOf('.') + 1, packageName.length());
    }
    public static <T> T getValueOfPrivateField(Object instance, String fieldName, Class<T> type) {
        Class<?> aClass = instance.getClass();
        Field declaredField;
        try {
            declaredField = aClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
        declaredField.setAccessible(true);
        Object o;
        try {
            o = declaredField.get(instance);
        } catch (IllegalAccessException e) {
            return null;
        }
        return RandomUtils.safeCast(o, type);
    }

    public static <T> T invokePrivateMethodAndGetReturn(Object instance, String method, Class<T> returnValueType) {
        Method declaredMethod;
        try {
            declaredMethod = instance.getClass().getDeclaredMethod(method);
        } catch (NoSuchMethodException e) {
            return null;
        }
        declaredMethod.setAccessible(true);
        Object invoke;
        try {
            invoke = declaredMethod.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
        return RandomUtils.safeCast(invoke, returnValueType);
    }

    public static <T> T invokeAccessableMethodAndReturn(Object instance, String method, Class<T> returnValueType) {
        try {
            return RandomUtils.safeCast(instance.getClass().getMethod(method).invoke(instance), returnValueType);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public static void invokeAccessableMethodWithArguments(Object instance, String method, Object... arguments) {
        Class<?>[] classes = new Class[arguments.length];
        for (int x = 0; x < arguments.length; x++) {
            classes[x] = arguments[x].getClass();
        }
        try {
            instance.getClass().getMethod(method, classes).invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
        }
    }

    public static void setValueOfDeclaredField(Object instance, String field, Object value) {
        Field declaredField;
        try {
            declaredField = instance.getClass().getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            return;
        }
        declaredField.setAccessible(true);
        try {
            declaredField.set(instance, value);
        } catch (IllegalAccessException ignored) {}
    }

    public static void invokePrivateMethodWithArguments(Object instance, String method, Object... arguments) {
        Class[] argumentTypes = new Class[arguments.length];
        for (int x = 0; x < arguments.length; x++) {
            argumentTypes[x] = arguments[x].getClass();
        }
        Method declaredMethod;
        try {
            declaredMethod = instance.getClass().getDeclaredMethod(method, argumentTypes);
        } catch (NoSuchMethodException e) {
            return;
        }
        declaredMethod.setAccessible(true);
        try {
            declaredMethod.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }

    public static Class<?> getCraftClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + minecraftVersion + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
