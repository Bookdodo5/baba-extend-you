package logic.input;

import application.GameController;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import utils.GraphicUtils;

import java.util.Set;
import java.util.HashSet;

import static application.Constant.COLOR_LIST;

/**
 * Utility class for handling keyboard input.
 */
public class InputUtility {
    private static final Set<KeyCode> keyPressed = new HashSet<>();
    private static final Set<KeyCode> keyTriggered = new HashSet<>();

    public static boolean isPressed(KeyCode key) {
        return keyPressed.contains(key);
    }

    public static boolean isTriggered(KeyCode key) {
        return keyTriggered.contains(key);
    }

    public static void setKeyPressed(KeyCode key, boolean pressed) {
        if(pressed) keyPressed.add(key);
        else keyPressed.remove(key);
    }

    public static InputCommand getTriggered() {
        if(isTriggered(KeyCode.ESCAPE)) {
            GraphicUtils.randomColorTheme();
            return InputCommand.MENU;
        }
        if(isTriggered(KeyCode.R)) return InputCommand.RESET;
        if(isTriggered(KeyCode.SPACE) || isTriggered(KeyCode.ENTER)) return InputCommand.TRIGGER;
        if(isTriggered(KeyCode.Z)) return InputCommand.UNDO;
        if(isTriggered(KeyCode.Y)) return InputCommand.REDO;
        if(isTriggered(KeyCode.UP) || isTriggered(KeyCode.W)) return InputCommand.MOVE_UP;
        if(isTriggered(KeyCode.DOWN) || isTriggered(KeyCode.S)) return InputCommand.MOVE_DOWN;
        if(isTriggered(KeyCode.LEFT) || isTriggered(KeyCode.A)) return InputCommand.MOVE_LEFT;
        if(isTriggered(KeyCode.RIGHT) || isTriggered(KeyCode.D)) return InputCommand.MOVE_RIGHT;
        return InputCommand.NONE;
    }

    public static InputCommand getPressed() {
        if(isPressed(KeyCode.ESCAPE)) return InputCommand.MENU;
        if(isPressed(KeyCode.R)) return InputCommand.RESET;
        if(isPressed(KeyCode.SPACE) || isPressed(KeyCode.ENTER)) return InputCommand.TRIGGER;
        if(isPressed(KeyCode.Z)) return InputCommand.UNDO;
        if(isPressed(KeyCode.Y)) return InputCommand.REDO;
        if(isPressed(KeyCode.UP) || isPressed(KeyCode.W)) return InputCommand.MOVE_UP;
        if(isPressed(KeyCode.DOWN) || isPressed(KeyCode.S)) return InputCommand.MOVE_DOWN;
        if(isPressed(KeyCode.LEFT) || isPressed(KeyCode.A)) return InputCommand.MOVE_LEFT;
        if(isPressed(KeyCode.RIGHT) || isPressed(KeyCode.D)) return InputCommand.MOVE_RIGHT;
        return InputCommand.NONE;
    }

    public static void setKeyTriggered(KeyCode key) {
        keyTriggered.add(key);
    }

    public static void clearTriggered() {
        keyTriggered.clear();
    }
}