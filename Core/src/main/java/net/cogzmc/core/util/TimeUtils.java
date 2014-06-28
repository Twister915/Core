package net.cogzmc.core.util;

import java.util.HashMap;
import java.util.Map;

public class TimeUtils {
    private static final Map<Character, Double> TIMEMAP = new HashMap<>();

    static {
        TIMEMAP.put('s', 1D);
        TIMEMAP.put('m', 60D);
        TIMEMAP.put('h', 3600D);
        TIMEMAP.put('d', 86400D);
        TIMEMAP.put('w', 604800D);
        TIMEMAP.put('y', 3.1536E7);
    }

    public static Double parseTime(String arg){
        char[] chars = arg.toCharArray();
        Double seconds = 0.0;
        for (int x = 0; x < chars.length; x++) {
            if (!TIMEMAP.containsKey(chars[x])) continue;
            Double multiplier = TIMEMAP.get(chars[x]);
            StringBuilder number = new StringBuilder();
            for (int cursor = x-1; cursor >= 0; cursor--) {
                char aChar = chars[cursor];
                if (!Character.isDigit(cursor)) break;
                number.append(aChar);
            }
            String s = number.reverse().toString();
            Double time;
            try {
                time = Double.valueOf(s);
            } catch (NumberFormatException e) {
                continue;
            }
            seconds += time*multiplier;
        }
        return seconds;
    }

}
