package model.action;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite action that aggregates multiple actions into one.
 */
public class CompositeAction implements Action {

    private final List<Action> actions;

    public CompositeAction() {
        this.actions = new ArrayList<>();
    }

    /**
     * Appends an action to this composite.
     *
     * @param action the action to add
     */
    public void add(Action action) {
        actions.add(action);
    }

    /**
     * Appends all actions from another composite action into this one.
     *
     * @param other the composite action to merge from
     */
    public void combine(CompositeAction other) {
        actions.addAll(other.actions);
    }

    /**
     * Returns the number of actions in this composite.
     *
     * @return the action count
     */
    public int size() {
        return actions.size();
    }

    /**
     * Returns the list of actions contained in this composite.
     *
     * @return the list of actions
     */
    public List<Action> getActions() {
        return actions;
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
