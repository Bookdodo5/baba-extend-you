package model.action;

import java.util.Stack;

/**
 * Manages a stack of actions to enable undo and redo functionality.
 */
public class ActionStack {

    Stack<CompositeAction> undoStack;
    Stack<CompositeAction> redoStack;

    public ActionStack() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    /**
     * Pushes a new composite action onto the undo stack and clears the redo stack.
     *
     * @param compositeAction the action to record
     */
    public void newAction(CompositeAction compositeAction) {
        undoStack.push(compositeAction);
        redoStack.clear();
    }

    /** Pops the latest action from the undo stack, undoes it, and pushes it onto the redo stack. */
    public void undo() {
        if(undoStack.isEmpty()) {
            return;
        }
        CompositeAction undoAction = undoStack.pop();
        undoAction.undo();
        redoStack.push(undoAction);
    }

    /** Pops the latest action from the redo stack, re-executes it, and pushes it onto the undo stack. */
    public void redo() {
        if(redoStack.isEmpty()) {
            return;
        }
        CompositeAction redoAction = redoStack.pop();
        redoAction.execute();
        undoStack.push(redoAction);
    }

    /** Clears both the undo and redo stacks. */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
