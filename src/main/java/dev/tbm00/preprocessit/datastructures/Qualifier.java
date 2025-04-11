package dev.tbm00.preprocessit.datastructures;

import dev.tbm00.preprocessit.model.matcher.QualifierMatcher;

/**
 * Qualifiers hold location, condition, values, and matcher
 * 
 * Feel free to change what this holds to better fit the goal.
 * (this implementation is just an idea of how it might work)
 * 
 * Each Compoment-attribute-qualifer is defined in a config.yml
 */
public class Qualifier {
    private int id;
    private String location;
    private String condition;
    private String value;
    private QualifierMatcher matcher;

    public Qualifier(int id, String location, String condition, String value) {
        this.id = id;
        this.location = location;
        this.condition = condition;
        this.value = value;
        //StaticUtil.log("qualifier: " + id + " " + location + " " + condition + " " + value);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocaiton(String location) {
        this.location = location;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QualifierMatcher getMatcher() {
        return matcher;
    }
}
