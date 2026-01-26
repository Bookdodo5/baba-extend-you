package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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

    private void updateScale(GameScreen screen, StackPane root) {
        double widthScale = root.getWidth() / TARGET_SCREEN_WIDTH;
        double heightScale = root.getHeight() / TARGET_SCREEN_HEIGHT;
        double scale = Math.min(widthScale, heightScale);
        screen.setScaleX(scale);
        screen.setScaleY(scale);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        stage.setWidth(TARGET_SCREEN_WIDTH);
        stage.setHeight(TARGET_SCREEN_HEIGHT);
        stage.setScene(scene);
        stage.show();

        LevelMap map = LevelLoader.loadLevel("mapTest.csv");

        LevelController logic = new LevelController();
        GameScreen screen = new GameScreen(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT);

        logic.setLevelMap(map);

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateScale(screen, root);
        });
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateScale(screen, root);
        });

        root.getChildren().add(screen);
        screen.requestFocus();

        AnimationTimer animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                screen.render();
                logic.update();
            }
        };

        animation.start();
    }
}
