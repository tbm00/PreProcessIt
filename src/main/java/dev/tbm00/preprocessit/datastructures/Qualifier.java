package dev.tbm00.preprocessit.datastructures;

/**
 * Qualifiers hold location, condition, and values
 */
public class Qualifier {
    private int id;
    private String location;
    private String condition;
    private String value;

    public Qualifier(int id, String location, String condition, String value) {
        this.id = id;
        this.location = location;
        this.condition = condition;
        this.value = value;
        //System.out.println("qualifier: " + id + " " + location + " " + condition + " " + value);
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
}
