package dev.tbm00.preprocessit.model.matcher;

import java.util.regex.Matcher;

import dev.tbm00.preprocessit.StaticUtil;

public class StartInBetweenExclusiveMatcher implements MatcherInterface {
    private double min;
    private double max;

    public StartInBetweenExclusiveMatcher(String values) {
        // expects "min,max"
        String[] parts = values.split(",");
        this.min = Double.parseDouble(parts[0]);
        this.max = Double.parseDouble(parts[1]);
    }
    
    @Override
    public String match(String word) {
        Matcher m = StaticUtil.NUMBER_PREFIX.matcher(word);
        if (!m.find()) return "";
        String prefix = m.group(1);
        double d = Double.parseDouble(prefix);
        return (d > min && d < max) ? prefix : "";
    }
}
