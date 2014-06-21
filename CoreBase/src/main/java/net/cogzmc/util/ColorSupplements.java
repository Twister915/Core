package net.cogzmc.util;

public final class ColorSupplements {
    private static final char COLOR_SYMBOL = '\u00A7'; //Section symbol
    public static String translateAlternateColorCodes(char character, String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int x = 1; x < stringBuilder.length(); x++ ) {
            char lastChar = stringBuilder.charAt(x-1);
            if (lastChar != character) continue;
            char c = stringBuilder.charAt(x);
            switch (c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    break;
                default:
                    continue;
            }
            stringBuilder.setCharAt(x-1, COLOR_SYMBOL);
        }
        return stringBuilder.toString();
    }
}
