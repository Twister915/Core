package net.communitycraft.core.model;

import com.google.common.collect.ImmutableList;

public interface ModelStorage<T extends Model> {
    ImmutableList<T> getValues();
    void saveValue(T value) throws SerializationException;
    void updateValue(T value) throws SerializationException;
    void deleteValue(T value);
    T findValue(String key, Object value);
    T getByKey(String key);
    void reload();
}
