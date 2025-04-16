package dev.tbm00.preprocessit;

import java.util.regex.Pattern;
public class StaticUtil {

    public static final Pattern NUMBER_PREFIX = Pattern.compile("^(\\d+(?:\\.\\d+)?)");
    public static final Pattern NUMBER_SUFFIX = Pattern.compile("(\\d+(?:\\.\\d+)?)$");

    public static void log(String string) {
        System.out.println(string);
    }

    public static void log(Object obj) {
        log(obj == null ? "null" : obj.toString());
    }

    public static void log() {
        log("");
    }
}