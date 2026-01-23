package view;

import logic.input.InputUtility;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static application.Constant.TARGET_SCREEN_WIDTH;
import static application.Constant.TARGET_SCREEN_HEIGHT;

public class GameScreen extends Canvas {
    public GameScreen(double width, double height) {
        super(width, height);
        this.setVisible(true);
        this.setFocusTraversable(true);
        this.setOnKeyPressed(event -> InputUtility.setKeyPressed(event.getCode(), true));
        this.setOnKeyReleased(event -> InputUtility.setKeyPressed(event.getCode(), false));
    }

    public void render() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, TARGET_SCREEN_WIDTH / 2, TARGET_SCREEN_HEIGHT / 2);
        gc.setFill(Color.RED);
        gc.fillRect(TARGET_SCREEN_WIDTH / 2, 0, TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT / 2);
        gc.setFill(Color.BLUE);
        gc.fillRect(TARGET_SCREEN_WIDTH / 2, TARGET_SCREEN_HEIGHT / 2, TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT);
        gc.setFill(Color.YELLOW);
        gc.fillRect(0, TARGET_SCREEN_HEIGHT / 2, TARGET_SCREEN_WIDTH / 2, TARGET_SCREEN_HEIGHT);

        gc.restore();
    }
}
