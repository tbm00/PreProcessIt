package dev.tbm00.preprocessit;

public class StaticUtil {

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