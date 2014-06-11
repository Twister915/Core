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
        //If we have it cached in our local "modelStorageMap" head to that to get the value
        if (modelStorageMap.containsKey(modelClass)) return (ModelStorage<T>) modelStorageMap.get(modelClass);
        //Otherwise, grab it from the database
        String collectionName = modelClass.getSimpleName().toLowerCase(); //Get the collection name
        if (collectionName.endsWith("model")) collectionName = collectionName.replaceFirst("model", ""); //If the class name ends in model, ignore it, little grammar thing
        DBCollection collection = database.getCollection(collectionName); //get the collection by name
        ModelSerializer<T> serializer = (ModelSerializer<T>) modelSerializers.get(modelClass); //Get the serializer for type T
        if (serializer == null) serializer = new DefaultModelSerializer(); //if we don't have it, grab the default one.
        MongoModelStorage<T> storage = new MongoModelStorage<>(collection, database, serializer, modelClass); //Create a model storage for this type
        this.modelStorageMap.put(modelClass, storage); //Put the storage in the map for the type
        storage.reload(); //Reload the storage
        return storage; //And hand it off
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
