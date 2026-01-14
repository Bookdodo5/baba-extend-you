package model.action;

import java.util.ArrayList;
import java.util.List;

public class CompositeAction implements Action {

    private final List<Action> actions;

    public CompositeAction() {
        this.actions = new ArrayList<>();
    }

    public void add(Action action) {
        actions.add(action);
    }

    @Override
    public void execute() {
        for (Action action : actions) {
            action.execute();
        }
    }

    @Override
    public void undo() {
        for (int i = actions.size() - 1; i >= 0; i--) {
            actions.get(i).undo();
        }
    }
}
