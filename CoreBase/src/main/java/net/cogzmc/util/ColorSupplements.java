package net.cogzmc.util;

public final class ColorSupplements {
    private static final char COLOR_SYMBOL = '\u00A7'; //Section symbol
    public static String translateAlternateColorCodes(char character, String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int x = 1; x < stringBuilder.length(); x++ ) {
            char lastChar = stringBuilder.charAt(x-1);
            if (lastChar != character) continue;
            char c = stringBuilder.charAt(x);
            int id = (int) c;
            if (!((id >= 49 && id <= 57) || (id >= 65 && id <= 75) || (id >= 97 && id <= 102))) continue;
            stringBuilder.setCharAt(x-1, COLOR_SYMBOL);
        }
        return stringBuilder.toString();
    }
}
