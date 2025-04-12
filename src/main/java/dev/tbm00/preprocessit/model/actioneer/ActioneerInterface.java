package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public interface ActioneerInterface {

    abstract void execute(Token token, ActionSpec actionSpec, String matchedString);
}