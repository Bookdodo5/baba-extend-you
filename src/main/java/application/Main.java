package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.input.InputUtility;
import state.GameStateEnum;
import utils.GraphicUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static application.Constant.TARGET_SCREEN_WIDTH;
import static application.Constant.TARGET_SCREEN_HEIGHT;

/**
 * Main class to launch the JavaFX application and initialize the game.
 */
public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        GameController gameController = GameController.getInstance();
        GameScreen screen = new GameScreen(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT);

        StackPane root = gameController.getRootPane();
        Scene scene = new Scene(root);
        stage.setWidth(TARGET_SCREEN_WIDTH);
        stage.setHeight(TARGET_SCREEN_HEIGHT);
        stage.setScene(scene);
        stage.show();

        screen.updateScale(root);
        root.widthProperty().addListener((_, _, _) -> screen.updateScale(root));
        root.heightProperty().addListener((_, _, _) -> screen.updateScale(root));

        root.getChildren().add(screen);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        GraphicUtils.applyPostProcessing(root);

        gameController.setState(GameStateEnum.TITLE);

        AnimationTimer animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    screen.render();
                    gameController.update();
                    InputUtility.clearTriggered();
                } catch (IllegalStateException e) {
                    System.err.println("CRITICAL ERROR: Game state corruption detected!");
                    System.err.println("Error details: " + e.getMessage());

                    gameController.setState(GameStateEnum.TITLE);
                }
            }
        };

        animation.start();
    }
}
