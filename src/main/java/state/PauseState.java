package state;

import application.GameController;
import application.Audio;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import logic.input.InputCommand;
import logic.input.InputUtility;
import model.rule.Rule;
import utils.GraphicUtils;
import utils.ImageUtils;

import java.io.File;
import java.util.List;

import static application.Constant.*;
import static utils.GraphicUtils.TEXT_SCALE;

/**
 * Represents the paused game state with a UI overlay showing menu options and active rules.
 */
public class PauseState implements GameState {

    private static final int MENU_BUTTON_WIDTH = 400;
    private static final int MENU_BUTTON_HEIGHT = (int) (FONT_HEIGHT * TEXT_SCALE);
    private static final int TOTAL_BUTTON = 4;
    private static final int SPACING = 4;

    private static final Image JAVA_IMAGE = ImageUtils.getImage("/sprite/JAVA.png");

    private VBox pauseOverlay;
    private ImageView selectIndicator;

    private int currentSelectedIndex = 0;

    /**
     * Initializes the pause screen UI when entering the state.
     */
    @Override
    public void onEnter(GameStateEnum previousState) {
        createOverlay();
        putOverlay();
        Audio.playSfx("sound/SFX/select.wav");
    }

    /**
     * Cleans up the pause screen UI when exiting.
     */
    @Override
    public void onExit() {
        removeOverlay();
        Audio.playSfx("sound/SFX/select.wav");
    }

    /**
     * If the player press the esc key, return to playing state.
     */
    @Override
    public void update() {
        InputCommand playerInput = InputUtility.getTriggered();
        if (playerInput == InputCommand.MENU) {
            Audio.playSfx("sound/SFX/esc.wav");
            resumeGame();
        }
        if (playerInput == InputCommand.MOVE_UP) {
            Audio.playSfx("sound/SFX/select.wav");
            currentSelectedIndex = (currentSelectedIndex + TOTAL_BUTTON - 1) % TOTAL_BUTTON;
        }
        if (playerInput == InputCommand.MOVE_DOWN) {
            Audio.playSfx("sound/SFX/select.wav");
            currentSelectedIndex = (currentSelectedIndex + 1) % TOTAL_BUTTON;
        }
        if (playerInput == InputCommand.TRIGGER) {
            Audio.playSfx("sound/SFX/confirm.wav");
            switch (currentSelectedIndex) {
                case 0 -> resumeGame();
                case 1 -> restartLevel();
                case 2 -> returnToMap();
                case 3 -> returnToMenu();
            }
        }

        GraphicUtils.updateIndicatorPosition(
                selectIndicator,
                currentSelectedIndex,
                MENU_BUTTON_HEIGHT, SPACING
        );
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
        pauseOverlay.setMinWidth(Screen.getPrimary().getBounds().getWidth());
        pauseOverlay.setPrefSize(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT);

        pauseOverlay.setAlignment(Pos.TOP_CENTER);
        pauseOverlay.setPadding(new Insets(10, 50, 50, 10));
        pauseOverlay.setSpacing(SPACING);
        pauseOverlay.setPickOnBounds(true);

        // Pause header
        String levelFilePath = GameController.getInstance().getCurrentLevelFilePath();
        String baseName = new File(levelFilePath).getName();
        String cleanedName = baseName.split("\\.")[0].replace("_", " ");
        HBox levelHeaderText = GraphicUtils.createTextNode(cleanedName);
        VBox.setMargin(levelHeaderText, new Insets(0, 0, 50, 0));

        // Buttons
        Button resumeButton = GraphicUtils.createButton("Resume", this::resumeGame, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        Button restartButton = GraphicUtils.createButton("Restart", this::restartLevel, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        Button returnToMapButton = GraphicUtils.createButton("Return to Map", this::returnToMap, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        Button returnToTitleButton = GraphicUtils.createButton("Return to Menu", this::returnToMenu, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        VBox.setMargin(returnToTitleButton, new Insets(0, 0, 20, 0));

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

        // Java sprite indicator
        selectIndicator = GraphicUtils.createButtonIndicator(JAVA_IMAGE, MENU_BUTTON_WIDTH);

        VBox buttonContainer = new VBox(SPACING);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(resumeButton, restartButton, returnToMapButton, returnToTitleButton);

        StackPane contentPane = new StackPane();
        contentPane.setAlignment(Pos.CENTER);
        contentPane.getChildren().addAll(buttonContainer, selectIndicator);

        pauseOverlay.getChildren().addAll(
                levelHeaderText,
                contentPane,
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
