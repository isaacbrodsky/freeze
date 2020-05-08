package com.isaacbrodsky.freeze2.oop;

public class OopUtils {
    public static String readWord(String oop, int start) {
        for (; start < oop.length() && oop.charAt(start) == ' '; start++) {
        }

        if (start >= oop.length()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        char curr = oop.charAt(start);
        if (curr < '0' || curr > '9') {
            while ((curr >= 'A' && curr <= 'Z')
                    || (curr >= 'a' && curr <= 'z')
                    || curr == ':'
                    || (curr >= '0' && curr <= '9')
                    || curr == '_') {
                sb.append(curr);
                start++;
                if (start == oop.length()) {
                    break;
                }
                curr = oop.charAt(start);
            }
        }

        return sb.toString();
    }
}
