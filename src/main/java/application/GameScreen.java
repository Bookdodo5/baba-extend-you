package application;

import javafx.scene.effect.Bloom;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import logic.input.InputUtility;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import static application.Constant.*;

/**
 * The main game canvas. Handles keyboard input registration and delegates rendering
 * to {@link GameController}.
 */
public class GameScreen extends Canvas {

    /**
     * Creates a new game screen with the specified dimensions and registers keyboard listeners.
     *
     * @param width  the canvas width in pixels
     * @param height the canvas height in pixels
     */
    public GameScreen(double width, double height) {
        super(width, height);
        setVisible(true);
        setFocusTraversable(true);
        setOnKeyPressed(event -> {
            if(!InputUtility.isPressed(event.getCode())) {
                InputUtility.setKeyTriggered(event.getCode());
            }
            InputUtility.setKeyPressed(event.getCode(), true);
        });
        setOnKeyReleased(event -> InputUtility.setKeyPressed(event.getCode(), false));
    }

    /** Clears the canvas and renders the current game state. */
    public void render() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.clearRect(0, 0, getWidth(), getHeight());

        GameController.getInstance().render(gc);
    }

    /**
     * Scales this canvas to fit within {@code root} while preserving the target aspect ratio.
     *
     * @param root the parent pane whose size determines the scale factor
     */
    public void updateScale(StackPane root) {
        double widthScale = root.getWidth() / TARGET_SCREEN_WIDTH;
        double heightScale = root.getHeight() / TARGET_SCREEN_HEIGHT;
        double scale = Math.min(widthScale, heightScale);
        setScaleX(scale);
        setScaleY(scale);
    }
}
