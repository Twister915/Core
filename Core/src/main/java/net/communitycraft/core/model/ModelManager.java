package net.communitycraft.core.model;

public interface ModelManager<DatabaseType> {
    <T extends Model> ModelStorage<T> getModelStorage(Class<T> modelClass);
    <T extends Model> void registerSerializer(ModelSerializer<T, DatabaseType> serializer, Class<T> modelType);
    <T extends Model> void unregisterSerializer(ModelSerializer<T, DatabaseType> serializer, Class<T> modelType);
    <T extends Model> ModelSerializer<T, DatabaseType> getSerializer(Class<T> modelType);
}
