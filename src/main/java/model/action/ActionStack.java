package model.action;

import java.util.Stack;

public class ActionStack {

    private record TurnAction(CompositeAction action, boolean isRuleReparse) {
    }

    Stack<TurnAction> undoStack;
    Stack<TurnAction> redoStack;

    public ActionStack() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public void newAction(CompositeAction action, boolean reparseRules) {
        undoStack.push(new TurnAction(action, reparseRules));
        redoStack.clear();
    }

    public boolean undo() {
        if(undoStack.empty()) return false;
        TurnAction undoAction = undoStack.pop();
        undoAction.action.undo();
        redoStack.push(undoAction);
        return undoAction.isRuleReparse;
    }

    public boolean redo() {
        if(redoStack.empty()) return false;
        TurnAction redoAction = redoStack.pop();
        redoAction.action.execute();
        undoStack.push(redoAction);
        return redoAction.isRuleReparse;
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
