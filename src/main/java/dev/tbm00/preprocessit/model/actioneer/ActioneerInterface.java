package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public interface ActioneerInterface {

    abstract String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log);
}