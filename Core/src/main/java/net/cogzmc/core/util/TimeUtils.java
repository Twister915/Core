package net.cogzmc.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joe on 6/27/2014.
 */
public class TimeUtils {

    private static final Map<String, Integer> TIMEMAP = new HashMap<>();

    static{
        TIMEMAP.put("s",1000);
        TIMEMAP.put("m",60000);
        TIMEMAP.put("h",3600000);
        TIMEMAP.put("d",24*60*60*1000);
        TIMEMAP.put("w",7*24*60*60*1000);
        TIMEMAP.put("y",365*24*60*60*1000);
    }

    public static Long parseTime(String arg){

        if(arg.length() < 2){
            return null;
        }
        String unit = arg.substring(arg.length()-1); //Last letter
        Integer multiplier = TIMEMAP.get(unit.toLowerCase());
        if(multiplier == null){
            return null;
        }
        Double time = null;
        try {
            Double.parseDouble(arg.substring(0, arg.length() - 2));
        }
        catch(Exception e){
            return null;
        }
        return new Long((long) (time*multiplier));
    }

}
