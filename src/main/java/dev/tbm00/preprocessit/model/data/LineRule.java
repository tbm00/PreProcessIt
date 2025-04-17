package dev.tbm00.preprocessit.model.data;

import java.util.ArrayList;

/**
 * LineRules hold qualifiers
 */
public class LineRule {
    private int id;
    private ArrayList<Qualifier> qualifiers;

    public LineRule(int id, ArrayList<Qualifier> qualifiers) {
        this.id = id;
        this.qualifiers = qualifiers;
        //StaticUtil.log("attribute: " + id + " " + name + " " + qualifiers);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public ArrayList<Qualifier> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers(ArrayList<Qualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }
}
