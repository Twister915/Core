package net.cogzmc.core.network;

import com.google.gson.*;

import java.util.Date;

public final class NetworkUtils {
    public static Gson getGson() {
        return new GsonBuilder().create();
    }

    public static Gson getGson(boolean allFields) {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return !(allFields || fieldAttributes.getAnnotation(NetCommandField.class) != null);
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).serializeNulls().create();
    }

    @SuppressWarnings("unchecked")
    public static JsonObject encodeNetCommand(NetCommand command) throws Exception {
        Class<? extends NetCommand> commandType = command.getClass(); //Command type

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(NetworkKeys.NET_COMMAND_TIME.getValue(), new Date().getTime());
        jsonObject.addProperty(NetworkKeys.NET_COMMAND_CLASS_NAME.getValue(), commandType.getName());
        JsonElement jsonElement = getGson(commandType.isAnnotationPresent(NetCommandField.class)).toJsonTree(command);
        jsonObject.add(NetworkKeys.NET_COMMAND_ARGUMENTS.getValue(), jsonElement);
        return jsonObject;
    }

    public static NetCommand decodeNetCommand(JsonObject object) throws Exception {
        //Get the class
        Class netCommandType;
        try {
            netCommandType = Class.forName((String) object.getAsJsonPrimitive(NetworkKeys.NET_COMMAND_CLASS_NAME.getValue()).getAsString());
        } catch (ClassNotFoundException ex) {
            return null;
        }

        JsonObject asJsonObject = object.getAsJsonObject(NetworkKeys.NET_COMMAND_ARGUMENTS.getValue());
        Gson gson = getGson(netCommandType.isAnnotationPresent(NetCommandField.class));
        return (NetCommand) gson.fromJson(asJsonObject, netCommandType);
    }
}
