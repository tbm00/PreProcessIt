package dev.tbm00.preprocessit.data;

import dev.tbm00.preprocessit.data.enums.Action;
import dev.tbm00.preprocessit.data.enums.Condition;
import dev.tbm00.preprocessit.data.enums.Location;
import dev.tbm00.preprocessit.model.matcher.QualifierMatcher;

/**
 * Qualifiers hold location, condition, values, and matcher
 * 
 * Feel free to change what this holds to better fit the goal.
 * (this implementation is just an idea of how it might work)
 * 
 * Each Compoment-attribute-qualifier is defined in a config.yml
 */
public class Qualifier {
    private int id;
    private Location[] locations;
    private Condition condition;
    private String value;
    private Action[] qualifiedActions;
    private Action[] unqualifiedActions;
    private QualifierMatcher matcher;

    public Qualifier(int id, Location[] locations, Condition condition, String value,
                     Action[] qualifiedActions, Action[] unqualifiedActions) {
        this.id = id;
        this.locations = locations;
        this.condition = condition;
        this.value = value;
        this.qualifiedActions = qualifiedActions;
        this.unqualifiedActions = unqualifiedActions;
    }

    // Getters and setters

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public Location[] getLocations() {
        return locations;
    }

    public void setLocations(Location[] locations) {
        this.locations = locations;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Action[] getQualifiedActions() {
        return qualifiedActions;
    }

    public void setQualifiedActions(Action[] qualifiedActions) {
        this.qualifiedActions = qualifiedActions;
    }

    public Action[] getUnqualifiedActions() {
        return unqualifiedActions;
    }

    public void setUnqualifiedActions(Action[] unqualifiedActions) {
        this.unqualifiedActions = unqualifiedActions;
    }

    public QualifierMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(QualifierMatcher matcher) {
        this.matcher = matcher;
    }
}