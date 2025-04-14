package dev.tbm00.preprocessit.model.data.enums;

public class ActionSpec {
    private final Action action;
    private final String parameter;

    public ActionSpec(Action action, String parameter) {
        this.action = action;
        this.parameter = parameter;
    }

    public Action getAction() {
        return action;
    }

    public String getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        if (parameter != null && !parameter.isEmpty()) {
            return action.name() + "(" + parameter + ")";
        }
        return action.name();
    }
}
