package net.cogzmc.core.json;

import com.google.gson.*;
import net.cogzmc.core.util.Point;
import net.cogzmc.core.util.Region;
import org.json.simple.JSONObject;

import java.lang.reflect.Type;

@Deprecated
@SuppressWarnings("unchecked")
public final class RegionSerializer implements JSONSerializer<Region>, JsonSerializer<Region>, JsonDeserializer<Region> {
    private final static String MIN = "min";
    private final static String MAX = "max";

    @Override
    public JSONObject serialize(Region object) {
        PointSerializer serializer = Point.getSerializer();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MIN, serializer.serialize(object.getMin()));
        jsonObject.put(MAX, serializer.serialize(object.getMax()));
        return jsonObject;
    }

    @Override
    public Region deserialize(JSONObject object) {
        PointSerializer serializer = Point.getSerializer();
        return new Region(serializer.deserialize((JSONObject) object.get(MIN)), serializer.deserialize((JSONObject) object.get(MAX)));
    }

    @Override
    public Region deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext deserializer) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        return new Region(((Point) deserializer.deserialize(obj.get(MIN), Point.class)), ((Point) deserializer.deserialize(obj.get(MAX), Point.class)));
    }

    @Override
    public JsonElement serialize(Region points, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add(MIN,jsonSerializationContext.serialize(points.getMin()));
        object.add(MAX,jsonSerializationContext.serialize(points.getMax()));
        return object;
    }
}
