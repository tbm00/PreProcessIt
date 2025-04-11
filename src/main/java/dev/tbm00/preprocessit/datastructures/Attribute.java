package dev.tbm00.preprocessit.datastructures;

import java.util.ArrayList;

/**
 * Attributes hold qualifiers
 */
public class Attribute {
    private int id;
    private String name;
    private ArrayList<Qualifier> qualifiers;

    public Attribute(int id, String name, ArrayList<Qualifier> qualifiers) {
        this.id = id;
        this.name = name;
        this.qualifiers = qualifiers;
        //StaticUtil.log("attribute: " + id + " " + name + " " + qualifiers);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Qualifier> getQualifiers() {
        return qualifiers;
    }

    public void setAttributes(ArrayList<Qualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }
}
