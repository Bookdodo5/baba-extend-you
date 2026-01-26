package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import logic.controller.GameController;
import logic.controller.GameState;
import logic.controller.LevelController;
import model.map.LevelLoader;
import model.map.LevelMap;
import view.GameScreen;

import static application.Constant.TARGET_SCREEN_WIDTH;
import static application.Constant.TARGET_SCREEN_HEIGHT;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        stage.setWidth(TARGET_SCREEN_WIDTH);
        stage.setHeight(TARGET_SCREEN_HEIGHT);
        stage.setScene(scene);
        stage.show();

        GameController gameController = GameController.getInstance();
        LevelController levelController = new LevelController();
        GameScreen screen = new GameScreen(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT);

        gameController.setLevelController(levelController);
        gameController.setGameScreen(screen);
        gameController.startGame();
        gameController.playMap("mapTest.csv");

        root.widthProperty().addListener((obs, oldVal, newVal) -> screen.updateScale(root));
        root.heightProperty().addListener((obs, oldVal, newVal) -> screen.updateScale(root));

        root.getChildren().add(screen);
        screen.requestFocus();

        AnimationTimer animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                screen.render();
                if(gameController.getState() == GameState.PLAYING) {
                    levelController.update();
                }
            }
        };

        animation.start();
    }
}
