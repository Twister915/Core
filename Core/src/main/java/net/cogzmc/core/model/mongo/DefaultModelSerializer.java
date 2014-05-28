package net.cogzmc.core.model.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.SneakyThrows;
import net.cogzmc.core.Core;
import net.cogzmc.core.model.Model;
import net.cogzmc.core.model.ModelField;
import net.cogzmc.core.model.ModelSerializer;
import net.cogzmc.core.netfiles.NetElement;
import net.cogzmc.core.network.NetworkServer;
import net.cogzmc.core.player.COfflinePlayer;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

final class DefaultModelSerializer<T extends Model> implements ModelSerializer<T> {
    @Override
    @SneakyThrows
    public Object serialize(T model) {
        DBObject object = new BasicDBObject();
        Class<? extends Model> modelClass = model.getClass();
        boolean typeAnnotated = modelClass.isAnnotationPresent(ModelField.class);
        ModelField annotation = typeAnnotated ? modelClass.getAnnotation(ModelField.class) : null;
        for (Field field : modelClass.getDeclaredFields()) {
            if (typeAnnotated == field.isAnnotationPresent(ModelField.class)) continue; //check NetCommand source for more info on how this works, very similar pattern there.
            if (!typeAnnotated) annotation = field.getAnnotation(ModelField.class);
            field.setAccessible(true);
            Object o = field.get(model);
            if (o == null && annotation.storeNulls()) continue; //If the field's value (o) is null and we cannot store nulls, continue.
            object.put(field.getName(), applyTypeFiltersForDB(o));
        }
        return object;
    }

    @Override
    @SneakyThrows
    public T deserialize(Object object, Class<T> modelClass) {
        Constructor<T> constructor;
        try {
            constructor = modelClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalClassException("You do not have a zero-args constructor for this model!");
        }
        constructor.setAccessible(true);
        T t = constructor.newInstance();
        DBObject dbObject = (DBObject)object;
        for (String key : dbObject.keySet()) {
            Field declaredField;
            try {
                declaredField = modelClass.getDeclaredField(key);
            } catch (NoSuchFieldException e) {
                continue;
            }
            declaredField.setAccessible(true);
            declaredField.set(t, applyTypeFiltersFromDB(dbObject.get(key), declaredField.getType()));
        }
        return t;
    }

    static Object applyTypeFiltersForDB(Object object) {
        if (object == null) return null;
        //If the value of this field is an offline player (or online, they're polymorphic), then we'll store the UUID
        if (object instanceof COfflinePlayer) return ((COfflinePlayer) object).getUniqueIdentifier().toString();
        //If the value of this field is an Enum, we'll store the name of the value
        if (object instanceof Enum) return ((Enum) object).name();
        //If the value of the field is in the NetFile system, let's store the ID, however this may change as this system is technically under TODO
        if (object instanceof NetElement) return ((NetElement) object).getId();
        //If the value is a Network server of some kind, we'll return the network server's name.
        if (object instanceof NetworkServer) return ((NetworkServer) object).getName();
        //Handle serialization of bukkit's vectors
        if (object instanceof org.bukkit.util.Vector) {
            Vector vector = (Vector) object;
            DBObject dbVector = new BasicDBObject();
            dbVector.put("x", vector.getX());
            dbVector.put("y", vector.getY());
            dbVector.put("z", vector.getZ());
            return dbVector;
        }
        if (object instanceof Location) {
            Location location = (Location) object;
            DBObject dbLocation = new BasicDBObject();
            dbLocation.put("x", location.getX());
            dbLocation.put("y", location.getY());
            dbLocation.put("z", location.getZ());
            dbLocation.put("pitch", location.getPitch());
            dbLocation.put("yaw", location.getYaw());
            dbLocation.put("world", location.getWorld().getName());
            return dbLocation;
        }
        //If the value is a map, let's do some hardcore processing.
        if (object instanceof Map) {
            Map map = (Map) object; //First cast since we use this a lot
            if (map.isEmpty()) return new BasicDBObject(); //Check if we're empty, if we are no one really cares.
            //Now, let's create a quick storage vessel starting with the embed flag key which should be a map flag.
            BasicDBObject dbMap = new BasicDBObject(MongoModelKeys.EMBEDDED_FLAG_KEY.toString(), MongoModelKeys.EMBEDDED_MAP_FLAG.toString());
            //And lastly throw the actual contents of the map (which will be generated by recursively going through this method again for each value) into the storage vessel.
            dbMap.put(MongoModelKeys.EMBEDDED_CONTENTS_KEY.toString(), prepareMapForDB(map));
            //and return it.
            return dbMap;
        }
        //Same process for the list, read the map comments and replace what makes sense for a list.
        if (object instanceof List) {
            List list = (List) object;
            if (list.isEmpty()) return new BasicDBList();
            BasicDBObject dbList = new BasicDBObject(MongoModelKeys.EMBEDDED_FLAG_KEY.toString(), MongoModelKeys.EMBEDDED_LIST_FLAG.toString());
            dbList.put(MongoModelKeys.EMBEDDED_CONTENTS_KEY.toString(), prepareListForDB(list));
            return dbList;
        }
        return object; //Return the object if we can't really do anything to it and hope for the best.
    }

    //In serious need of commenting
    //This is responsible for reading a *part* of a DBObject (a single value) and attempting to convert it to a destination type.
    static Object applyTypeFiltersFromDB(Object object, Class type) {
        //If the type we're trying to get to is a COfflinePlayer, and we have a String in the database, let's assume that string is the UUID and grab the player with that UUID
        if (COfflinePlayer.class.isAssignableFrom(type) && object instanceof String) Core.getPlayerManager().getOfflinePlayerByUUID(UUID.fromString((String) object));
        //If the type we're trying to get to is an Enum, and we have a String in the database, let's try and get the enum value by name based on the string in the db.
        if (Enum.class.isAssignableFrom(type) && object instanceof String) Enum.valueOf(type, (String) object);
        //TODO NetFile
        //If the type we're trying to get is a NetworkServer, and we've got a String in the db, let's grab a known server by that name.
        if (NetworkServer.class.isAssignableFrom(type) && object instanceof String) return Core.getNetworkManager().getServer((String) object);
        if (Vector.class.isAssignableFrom(type) && object instanceof BasicDBObject) {
            BasicDBObject dbVector = (BasicDBObject) object;
            double x = dbVector.getDouble("x");
            double y = dbVector.getDouble("y");
            double z = dbVector.getDouble("z");
            return new Vector(x, y, z);
        }
        if (Location.class.isAssignableFrom(type) && object instanceof BasicDBObject) {
            BasicDBObject dbLocation = (BasicDBObject) object;
            double x = dbLocation.getDouble("x");
            double y = dbLocation.getDouble("y");
            double z = dbLocation.getDouble("z");
            float pitch = (float) dbLocation.get("pitch");
            float yaw = (float) dbLocation.get("yaw");
            String world = dbLocation.getString("world");
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        }
        //If we're trying to get to a map and we have a BasicDBObject, let's do some digging
        if (Map.class.isAssignableFrom(type) && object instanceof BasicDBObject) {
            //First, let's cast for ease of use
            BasicDBObject dbObject = (BasicDBObject) object;
            //Then, check if this is one of those with the EMBEDDED system (which involves some serious logic). If not, let's just process this raw.
            Map<String, Object> stringObjectMap = processMapFromDB(dbObject, Object.class);
            if (!dbObject.containsKey(MongoModelKeys.EMBEDDED_FLAG_KEY.toString())) return stringObjectMap;
            if (!dbObject.get(MongoModelKeys.EMBEDDED_FLAG_KEY.toString()).equals(MongoModelKeys.EMBEDDED_MAP_FLAG)) return stringObjectMap;
            //Since it's in the Embedded system, we'll get the actual contents of the map using the EMBEDDED_CONTENTS_KEY
            BasicDBObject actualMap = (BasicDBObject) dbObject.get(MongoModelKeys.EMBEDDED_CONTENTS_KEY.toString());
            //And we'll do a quick null check, returning a processed raw map if it fails
            if (actualMap == null) return stringObjectMap;
            //Otherwise, let's repeat this process over again recursively, and send it off to the map processor
            HashMap<String, Object> actualMapFinal = new HashMap<>();
            for (Map.Entry<String, Object> stringObjectEntry : actualMap.entrySet()) {
                actualMapFinal.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            }
            return actualMapFinal;
        }
        //See above for a more narrative like explanation on how this works.
        if (List.class.isAssignableFrom(type) && object instanceof BasicDBObject) {
            //Grab the object
            BasicDBObject dbObject = (BasicDBObject) object;
            //Check the flags, get the contents and type
            if (!dbObject.get(MongoModelKeys.EMBEDDED_FLAG_KEY.toString()).equals(MongoModelKeys.EMBEDDED_LIST_FLAG)) return null;
            BasicDBList actualList = (BasicDBList) dbObject.get(MongoModelKeys.EMBEDDED_CONTENTS_KEY.toString());
            if (actualList == null) return null;
            //Process
            ArrayList<Object> objects = new ArrayList<>();
            for (Object o : actualList) {
                objects.add(o);
            }
            return objects;
        }
        //Return the object if we've yet to return.
        return object;
    }

    static <T> List<T> processListFromDB(BasicDBList list, Class<T> type) {
        List<T> returnableList = new ArrayList<>();
        for (Object o : list) {
            try {
                //noinspection unchecked
                returnableList.add((T) o);
            } catch (ClassCastException ignored) {}
        }
        return returnableList;
    }

    @SuppressWarnings("unchecked")
    static <T> Map<String, T> processMapFromDB(DBObject object, Class<T> valueType) {
        Map<String, T> map = new HashMap<>();
        for (String s : object.keySet()) {
            try {
                T o = (T)object.get(s);
                map.put(s, (T) applyTypeFiltersFromDB(o, valueType));
            } catch (ClassCastException ignored){}
        }
        return map;
    }

    static DBObject prepareMapForDB(Map<?, ?> map) {
        BasicDBObject basicDBObject = new BasicDBObject();
        for (Map.Entry<?, ?> stringEntry : map.entrySet()) {
            basicDBObject.put(stringEntry.getKey().toString(), applyTypeFiltersForDB(stringEntry.getValue()));
        }
        return basicDBObject;
    }

    static BasicDBList prepareListForDB(List<?> list) {
        BasicDBList dbList = new BasicDBList();
        for (Object o : list) {
            dbList.add(applyTypeFiltersForDB(o));
        }
        return dbList;
    }
}
