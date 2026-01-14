package model.action;

import java.util.Stack;

public class ActionStack {
    Stack<CompositeAction> undoStack;
    Stack<CompositeAction> redoStack;

    public ActionStack() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public void newAction(CompositeAction action) {
        undoStack.push(action);
        redoStack.clear();
    }

    public void undo() {
        if(undoStack.empty()) return;
        CompositeAction action = undoStack.pop();
        action.undo();
        redoStack.push(action);
    }

    public void redo() {
        if(redoStack.empty()) return;
        CompositeAction action = redoStack.pop();
        action.execute();
        undoStack.push(action);
    }
}
