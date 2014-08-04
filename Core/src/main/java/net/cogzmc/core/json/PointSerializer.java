package net.cogzmc.core.json;

import net.cogzmc.core.util.Point;
import org.json.simple.JSONObject;

public final class PointSerializer implements JSONSerializer<Point> {
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
}
