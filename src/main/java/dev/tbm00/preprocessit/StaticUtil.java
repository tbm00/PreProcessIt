package dev.tbm00.preprocessit;

import java.util.regex.Pattern;

public class StaticUtil {

    public static final String KEY_CONCURRENT_THREADING = "concurrentThreading";
    public static final String KEY_CONCURRENT_OVERRIDE = "threadPoolSizeOverride";

    public static final String KEY_COMPONENTS = "components";
    public static final String KEY_INPUT_LINE_RULES = "inputLineRules";
    public static final String KEY_OUTPUT_LINE_RULES = "outputLineRules";
    public static final String KEY_ATTRIBUTE_OUTPUT_ORDER = "attributeOutputOrder";
    public static final String KEY_ATTRIBUTE_OUTPUT_DELIMITER = "attributeOutputDelimiter";
    public static final String KEY_ATTRIBUTES = "attributes";
    public static final String KEY_WORD = "word";
    public static final String KEY_CONDITION = "condition";
    public static final String KEY_VALUE = "value";
    public static final String KEY_QUALIFIED_ACTIONS = "qualifiedActions";
    public static final String KEY_UNQUALIFIED_ACTIONS = "unqualifiedActions";

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