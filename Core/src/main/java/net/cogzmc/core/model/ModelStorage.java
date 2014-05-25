package net.cogzmc.core.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public interface ModelStorage<T extends Model> {
    ImmutableList<T> getValues();
    void saveValue(T value) throws SerializationException;
    void updateValue(T value) throws SerializationException;
    void deleteValue(T value);
    T findValue(String key, Object value);
    List<T> findValues(String key, Object value);
    T getById(String key);
    void reload();
}

