package dev.tbm00.preprocessit.model.data;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;
import dev.tbm00.preprocessit.model.data.enums.Condition;
import dev.tbm00.preprocessit.model.data.enums.WordSpec;
import dev.tbm00.preprocessit.model.matcher.MatcherInterface;
import dev.tbm00.preprocessit.model.matcher.MatcherFactory;

/**
 * Qualifiers holds word, condition, values, actionSpecs, and matcher
 * 
 * Each Compoment-attribute-qualifier is defined in a config.yml
 */
public class Qualifier {
    private int id;
    private WordSpec wordSpec;
    private Condition condition;
    private String values;
    private ActionSpec[] qualifiedActions;
    private ActionSpec[] unqualifiedActions;
    private MatcherInterface matcher; 

    public Qualifier(int id, WordSpec wordSpec, Condition condition, String values,
                    ActionSpec[] qualifiedActions, ActionSpec[] unqualifiedActions) {
        this.id = id;
        this.wordSpec = wordSpec;
        this.condition = condition;
        this.values = values;
        this.qualifiedActions = qualifiedActions;
        this.unqualifiedActions = unqualifiedActions;
        matcher = MatcherFactory.createMatcher(condition, values);
    }

    // Getters and setters

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public WordSpec getWordSpec() {
        return wordSpec;
    }

    public void setWordSpec(WordSpec wordSpec) {
        this.wordSpec = wordSpec;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getValues() {
        return values;
    }

    public void setValue(String values) {
        this.values = values;
    }

    public ActionSpec[] getQualifiedActions() {
        return qualifiedActions;
    }

    public void setQualifiedActions(ActionSpec[] qualifiedActions) {
        this.qualifiedActions = qualifiedActions;
    }

    public ActionSpec[] getUnqualifiedActions() {
        return unqualifiedActions;
    }

    public void setUnqualifiedActions(ActionSpec[] unqualifiedActions) {
        this.unqualifiedActions = unqualifiedActions;
    }

    public MatcherInterface getMatcher() {
        return matcher;
    }

    public void setMatcher(MatcherInterface matcher) {
        this.matcher = matcher;
    }
}