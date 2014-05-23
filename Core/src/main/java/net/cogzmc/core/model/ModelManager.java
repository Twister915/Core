package net.cogzmc.core.model;

public interface ModelManager {
    <T extends Model> ModelStorage<T> getModelStorage(Class<T> modelClass);
    <T extends Model> void registerSerializer(ModelSerializer<T> serializer, Class<T> modelType);
    <T extends Model> void unregisterSerializer(ModelSerializer<T> serializer, Class<T> modelType);
    <T extends Model> ModelSerializer<T> getSerializer(Class<T> modelType);
}
