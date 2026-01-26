package view;

import javafx.scene.layout.StackPane;
import logic.controller.GameController;
import logic.input.InputUtility;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.entity.Entity;
import model.map.LevelMap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static application.Constant.*;

public class GameScreen extends Canvas {

    private long lastPrintTime = 0L;

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

        LevelMap levelMap = GameController.getInstance().getCurrentMap();
        if(levelMap == null) {
            return;
        }

        if(System.currentTimeMillis() - lastPrintTime < 500L) {
            return;
        }
        lastPrintTime = System.currentTimeMillis();

        displayLevelMapInConsole(levelMap);
    }

    public void updateScale(StackPane root) {
        double widthScale = root.getWidth() / TARGET_SCREEN_WIDTH;
        double heightScale = root.getHeight() / TARGET_SCREEN_HEIGHT;
        double scale = Math.min(widthScale, heightScale);
        setScaleX(scale);
        setScaleY(scale);
    }

    private void displayLevelMapInConsole(LevelMap levelMap) {
        StringBuilder frame = new StringBuilder("\033[H");
        for (int y = 0; y < levelMap.getHeight(); y++) {
            for (int x = 0; x < levelMap.getWidth(); x++) {
                String cellContent = formatCell(levelMap.getEntitiesAt(x, y));
                frame.append(String.format("%-3s", cellContent));
            }
            frame.append("\n");
        }
        System.out.printf(frame.toString());
    }

    private String formatCell(List<Entity> entities) {
        if (entities.isEmpty()) {
            return ".";
        }

        if (entities.size() == 1) {
            return Arrays.stream(entities.getFirst().getType().getTypeId().split("_")).toList().getLast().toUpperCase().charAt(0) + "";
        }

        return entities.stream()
                .map(entity -> Arrays.stream(entity.getType().getTypeId().split("_")).toList().getLast().toUpperCase().charAt(0) + "")
                .collect(Collectors.joining(""));
    }
}
