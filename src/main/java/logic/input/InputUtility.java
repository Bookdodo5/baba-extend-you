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

    /**
     * Returns whether the given key is currently held down.
     *
     * @param key the key to query
     * @return {@code true} if the key is pressed
     */
    public static boolean isPressed(KeyCode key) {
        return keyPressed.contains(key);
    }

    /**
     * Returns whether the given key was triggered (pressed) this frame.
     *
     * @param key the key to query
     * @return {@code true} if the key was triggered
     */
    public static boolean isTriggered(KeyCode key) {
        return keyTriggered.contains(key);
    }

    /**
     * Updates the pressed state of the given key.
     *
     * @param key     the key to update
     * @param pressed {@code true} to mark the key as pressed, {@code false} to release it
     */
    public static void setKeyPressed(KeyCode key, boolean pressed) {
        if(pressed) keyPressed.add(key);
        else keyPressed.remove(key);
    }

    /**
     * Returns the {@link InputCommand} mapped to the first triggered key this frame,
     * also applying side effects such as randomizing the color theme on ESCAPE.
     *
     * @return the triggered command, or {@link InputCommand#NONE} if no relevant key was triggered
     */
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

    /**
     * Returns the {@link InputCommand} mapped to the first currently held key.
     *
     * @return the held command, or {@link InputCommand#NONE} if no relevant key is held
     */
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

    /**
     * Marks the given key as triggered for this frame.
     *
     * @param key the key to mark as triggered
     */
    public static void setKeyTriggered(KeyCode key) {
        keyTriggered.add(key);
    }

    /** Clears all triggered keys at the end of a frame. */
    public static void clearTriggered() {
        keyTriggered.clear();
    }
}