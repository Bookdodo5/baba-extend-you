package state;

import application.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import logic.input.InputCommand;
import logic.input.InputUtility;
import model.rule.Condition;
import model.rule.Rule;
import utils.GraphicUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static application.Constant.*;

/**
 * Represents the paused game state with a UI overlay showing menu options and active rules.
 */
public class PauseState implements GameState {

    private VBox pauseOverlay;

    /**
     * Initializes the pause screen UI when entering the state.
     */
    @Override
    public void onEnter(GameStateEnum previousState) {
        createOverlay();
        putOverlay();
        // TODO (SOUND): play menu enter sound
    }

    /**
     * Cleans up the pause screen UI when exiting.
     */
    @Override
    public void onExit() {
        removeOverlay();
        // TODO (SOUND): play menu exit sound
    }

    /**
     * If the player press the esc key, return to playing state.
     */
    @Override
    public void update() {
        InputCommand playerInput = InputUtility.getTriggered();
        if (playerInput == InputCommand.MENU) {
            resumeGame();
        }
    }

    /**
     * Renders the paused game state with a darkened overlay.
     */
    @Override
    public void render(GraphicsContext gc) {

        PlayingState playingState = (PlayingState) GameController.getInstance().getGameState(GameStateEnum.PLAYING);
        if (playingState != null) {
            playingState.render(gc);
        }

        Color colorTheme = GameController.getInstance().getColorTheme();
        Color translucentColor = colorTheme.interpolate(Color.TRANSPARENT, 0.4);
        gc.setFill(translucentColor.darker());
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private void createOverlay() {
        pauseOverlay = new VBox();
        pauseOverlay.setPrefSize(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT);

        pauseOverlay.setAlignment(Pos.TOP_CENTER);
        pauseOverlay.setPadding(new Insets(10, 50, 50, 10));
        pauseOverlay.setSpacing(4);
        pauseOverlay.setPickOnBounds(true);

        // Pause header
        String levelFilePath = GameController.getInstance().getCurrentLevelFilePath();
        String baseName = new File(levelFilePath).getName();
        String cleanedName = baseName.split("\\.")[0].replace("_", " ");
        HBox levelHeaderText = GraphicUtils.createTextNode(cleanedName);
        VBox.setMargin(levelHeaderText, new Insets(0, 0, 50, 0));

        // Buttons
        Button resumeButton = GraphicUtils.createButton("Resume", this::resumeGame, MENU_BUTTON_WIDTH);
        Button restartButton = GraphicUtils.createButton("Restart", this::restartLevel, MENU_BUTTON_WIDTH);
        Button returnToMapButton = GraphicUtils.createButton("Return to Map", this::returnToMap, MENU_BUTTON_WIDTH);
        Button returnToMenuButton = GraphicUtils.createButton("Return to Menu", this::returnToMenu, MENU_BUTTON_WIDTH);
        VBox.setMargin(returnToMenuButton, new Insets(20, 0, 20, 0));

        // Rules section
        HBox rulesHeaderText = GraphicUtils.createTextNode("Rules:");
        FlowPane rulesPane = new FlowPane(Orientation.VERTICAL);
        rulesPane.setAlignment(Pos.TOP_CENTER);
        rulesPane.setVgap(-5);
        rulesPane.setHgap(20);

        PlayingState playingState = (PlayingState) GameController.getInstance().getGameState(GameStateEnum.PLAYING);
        if (playingState != null) {
            List<Rule> rules = playingState.getLevelController().getRuleset().getRules();
            for (Rule rule : rules) {
                HBox ruleNode = GraphicUtils.createTextNode(rule.toString());
                rulesPane.getChildren().add(ruleNode);
            }
        }

        pauseOverlay.getChildren().addAll(
                levelHeaderText,
                resumeButton,
                restartButton,
                returnToMapButton,
                returnToMenuButton,
                rulesHeaderText,
                rulesPane
        );
    }

    private void putOverlay() {
        StackPane rootPane = GameController.getInstance().getRootPane();
        if (rootPane != null && pauseOverlay != null) {
            rootPane.getChildren().add(pauseOverlay);
        }
    }

    private void removeOverlay() {
        StackPane rootPane = GameController.getInstance().getRootPane();
        if (rootPane != null && pauseOverlay != null) {
            rootPane.getChildren().remove(pauseOverlay);
        }
    }

    private void resumeGame() {
        GameController.getInstance().setState(GameStateEnum.PLAYING);
    }

    private void restartLevel() {
        PlayingState playingState = (PlayingState) GameController.getInstance().getGameState(GameStateEnum.PLAYING);
        playingState.getLevelController().handleReset();
        GameController.getInstance().setState(GameStateEnum.PLAYING);
    }

    private void returnToMap() {
        GameController.getInstance().setState(GameStateEnum.MAP);
    }

    private void returnToMenu() {
        GameController.getInstance().setState(GameStateEnum.TITLE);
    }
}
