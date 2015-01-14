package net.cogzmc.core.network;

import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.util.Date;

public final class NetworkUtils {
    @SuppressWarnings("unchecked")
    public static JSONObject encodeNetCommand(NetCommand command) throws Exception {
        JSONObject object = new JSONObject(); //Create a holder for this NetCommand
        Class<? extends NetCommand> commandType = command.getClass(); //Command type
        object.put(NetworkKeys.NET_COMMAND_CLASS_NAME.getValue(), commandType.getName()); //Put the class name
        //Find the objects and values
        JSONObject arguments = new JSONObject();
        boolean allFields = commandType.isAnnotationPresent(NetCommandField.class); //Denotes if we should assume all fields have NetCommandField
        //Gets all the fields
        for (Field field : commandType.getDeclaredFields()) {
            boolean annotationPresent = field.isAnnotationPresent(NetCommandField.class);
            /*
             * this is short for short for (annotationPresent && allFields) || (!annotationPresent && !allFields)
             *
             * If the annotation is present on a field and on the type (annotationPresent && allFields) then we ignore the field
             * If the annotation is present on neither the field or type (!annotationPresent && !allFields) then we also ignore the field
             *
             * In any case where one of these did not agree, true/false or false/true true && false || true && false would be false, so it only works when false/false or true/true
             * True always equals true and false always equals false, thus this statement works.
             */
            if (allFields == annotationPresent) continue;
            //And adds them when they have a NetCommandField annotation.
            arguments.put(field.getName(), field.get(command));
        }
        object.put(NetworkKeys.NET_COMMAND_ARGUMENTS.getValue(), arguments);
        object.put(NetworkKeys.NET_COMMAND_TIME.getValue(), new Date().getTime());
        return object;
    }

    public static NetCommand decodeNetCommand(JSONObject object) throws Exception {
        //Get the class
        Class netCommandType;
        try {
            netCommandType = Class.forName((String) object.get(NetworkKeys.NET_COMMAND_CLASS_NAME.getValue()));
        } catch (ClassNotFoundException ex) {
            return null;
        }
        //Create a new instance of the NetCommand class that we found. THIS REQUIRES A NO ARGS CONSTRUCTOR TO BE PRESENT.
        NetCommand netCommand1 = (NetCommand) netCommandType.newInstance();
        JSONObject arguments = (JSONObject)object.get(NetworkKeys.NET_COMMAND_ARGUMENTS.getValue()); //Get the arguments
        boolean allFields = netCommandType.isAnnotationPresent(NetCommandField.class);
        for (Field field : netCommandType.getDeclaredFields()) { //And set the values in the class by
            if (!allFields && !field.isAnnotationPresent(NetCommandField.class)) continue; //Finding fields with this annotation
            field.setAccessible(true); //setting them accessible
            field.set(netCommand1, field.getType().cast(arguments.get(field.getName()))); //and setting their value
        }
        return netCommand1;
    }
}
