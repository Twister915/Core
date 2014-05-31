package net.cogzmc.core.model.mongo;

import com.mongodb.DBCollection;
import lombok.Data;
import net.cogzmc.core.model.Model;
import net.cogzmc.core.model.ModelManager;
import net.cogzmc.core.model.ModelSerializer;
import net.cogzmc.core.model.ModelStorage;
import net.cogzmc.core.player.mongo.CMongoDatabase;

import java.util.HashMap;
import java.util.Map;

@Data
public class MongoModelManager implements ModelManager {
    private final CMongoDatabase database;

    private final Map<Class<? extends Model>, ModelSerializer<?>> modelSerializers = new HashMap<>();
    private final Map<Class<? extends Model>, ModelStorage<?>> modelStorageMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> ModelStorage<T> getModelStorage(Class<T> modelClass) {
        if (modelStorageMap.containsKey(modelClass)) return (ModelStorage<T>) modelStorageMap.get(modelClass);
        String collectionName = modelClass.getSimpleName().toLowerCase();
        if (collectionName.endsWith("model")) collectionName = collectionName.replaceFirst("model", "");
        DBCollection collection = database.getCollection(collectionName);
        ModelSerializer<T> serializer = (ModelSerializer<T>) modelSerializers.get(modelClass);
        if (serializer == null) serializer = new DefaultModelSerializer();
        MongoModelStorage<T> storage = new MongoModelStorage<>(collection, database, serializer, modelClass);
        this.modelStorageMap.put(modelClass, storage);
        storage.reload();
        return storage;
    }

    @Override
    public <T extends Model> void registerSerializer(ModelSerializer<T> serializer, Class<T> modelType) {
        this.modelSerializers.put(modelType, serializer);
    }

    @Override
    public <T extends Model> void unregisterSerializer(ModelSerializer<T> serializer, Class<T> modelType) {
        this.modelSerializers.remove(modelType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> ModelSerializer<T> getSerializer(Class<T> modelType) {
        return (ModelSerializer<T>) this.modelSerializers.get(modelType);
    }
}
