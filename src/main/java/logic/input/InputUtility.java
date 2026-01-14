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
}
