package application;

import javafx.scene.effect.Bloom;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import logic.input.InputUtility;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import static application.Constant.*;

public class GameScreen extends Canvas {

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

    public void render() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.clearRect(0, 0, getWidth(), getHeight());

        GameController.getInstance().render(gc);
    }

    public void updateScale(StackPane root) {
        double widthScale = root.getWidth() / TARGET_SCREEN_WIDTH;
        double heightScale = root.getHeight() / TARGET_SCREEN_HEIGHT;
        double scale = Math.min(widthScale, heightScale);
        setScaleX(scale);
        setScaleY(scale);
    }
}
