package com.example.graduationprojectsingle.utils.stringUtils;

public class StringUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }
}
