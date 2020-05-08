package com.isaacbrodsky.freeze2.utils;

public class StringUtils {
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }

    public static String padLeft(String s, int len, char c) {
        int needed = Math.max(len - s.length(), 0);
        StringBuilder sb = new StringBuilder(needed);
        for (int i = 0; i < needed; i++) {
            sb.append(c);
        }
        return sb.toString() + s;
    }

    public static String padRight(String s, int len, char c) {
        int needed = Math.max(len - s.length(), 0);
        StringBuilder sb = new StringBuilder(needed);
        for (int i = 0; i < needed; i++) {
            sb.append(c);
        }
        return s + sb.toString();
    }
}
