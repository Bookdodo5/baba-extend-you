package logic.input;

import javafx.scene.input.KeyCode;

import java.util.Set;
import java.util.HashSet;

public class InputUtility {
    private static final Set<KeyCode> keyPressed = new HashSet<>();

    public static boolean isPressed(KeyCode key) {
        return keyPressed.contains(key);
    }

    public static void setKeyPressed(KeyCode key, boolean pressed) {
        if(pressed) keyPressed.add(key);
        else keyPressed.remove(key);
    }

    public static InputCommand getInputCommand() {
        if (isPressed(KeyCode.W)) return InputCommand.MOVE_UP;
        if (isPressed(KeyCode.S)) return InputCommand.MOVE_DOWN;
        if (isPressed(KeyCode.A)) return InputCommand.MOVE_LEFT;
        if (isPressed(KeyCode.D)) return InputCommand.MOVE_RIGHT;
        if (isPressed(KeyCode.Z)) return InputCommand.UNDO;
        if (isPressed(KeyCode.Y)) return InputCommand.REDO;
        if (isPressed(KeyCode.R)) return InputCommand.RESET;
        if (isPressed(KeyCode.ESCAPE)) return InputCommand.MENU;
        if (isPressed(KeyCode.SPACE)) return InputCommand.WAIT;
        return InputCommand.NONE;
    }
}