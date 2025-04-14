package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public interface ActioneerInterface {

    abstract String execute(String word, ActionSpec actionSpec, String matchedString);
}