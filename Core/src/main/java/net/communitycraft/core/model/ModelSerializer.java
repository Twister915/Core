package net.communitycraft.core.model;

public interface ModelSerializer<ModelType extends Model, DatabaseType> {
    DatabaseType serialize(ModelType model) throws SerializationException;
    ModelType deserialize(DatabaseType type, Class<ModelType> modelTypeClass) throws SerializationException;
}
