package net.cogzmc.core.model;

public interface ModelSerializer<ModelType extends Model> {
    Object serialize(ModelType model) throws SerializationException;
    ModelType deserialize(Object type, Class<ModelType> modelTypeClass) throws SerializationException;
    Object sanatizeObject(Object object) throws SerializationException;
}
