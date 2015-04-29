package net.cogzmc.core.json;

import com.google.gson.*;
import net.cogzmc.core.util.Point;
import org.json.simple.JSONObject;

import java.lang.reflect.Type;

@Deprecated
public final class PointSerializer implements JSONSerializer<Point>, JsonDeserializer<Point>, JsonSerializer<Point> {
    private final static String X = "x";
    private final static String Y = "y";
    private final static String Z = "z";
    private final static String PITCH = "p";
    private final static String YAW = "ya";

    @Override
    public JSONObject serialize(Point object) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(X, object.getX());
        jsonObject.put(Y, object.getY());
        jsonObject.put(Z, object.getZ());
        jsonObject.put(PITCH, object.getPitch());
        jsonObject.put(YAW, object.getYaw());
        return jsonObject;
    }

    @Override
    public Point deserialize(JSONObject object) {
        return Point.of(
                (Double)object.get(X),
                (Double)object.get(Y),
                (Double)object.get(Z),
                object.containsKey(PITCH) ? ((Double)object.get(PITCH)).floatValue() : 0f,
                object.containsKey (YAW) ? ((Double)object.get(YAW)).floatValue() : 0f);
    }

    @Override
    public Point deserialize(JsonElement obj2, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = obj2.getAsJsonObject();
        return Point.of(
                object.get(X).getAsDouble(),
                object.get(Y).getAsDouble(),
                object.get(Z).getAsDouble(),
                object.has(PITCH) ? object.get(PITCH).getAsFloat() : 0f,
                object.has(YAW) ? object.get(YAW).getAsFloat() : 0f);
    }

    @Override
    public JsonElement serialize(Point point, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty(X,point.getX());
        object.addProperty(Y,point.getY());
        object.addProperty(Z,point.getZ());
        if(point.getPitch() != null && point.getPitch() != 0f){
            object.addProperty(PITCH,point.getPitch());
        }
        if(point.getYaw() != null && point.getYaw() != 0f){
            object.addProperty(YAW,point.getYaw());
        }
        return object;
    }
}
