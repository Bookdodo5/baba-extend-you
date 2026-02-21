package state;

import application.GameController;
import application.Music;
import javafx.scene.canvas.GraphicsContext;
import logic.input.InputCommand;
import logic.input.InputUtility;

public class MapState implements GameState {
    /**
     *
     */
    @Override
    public void onEnter(GameStateEnum previousState) {
        // Code here happens when entering the level selector (world map)
        Music.play("sound/SFX/startStage.wav");
        Music.play("sound/music/Pixel_Quest_MainTheme.wav");
    }

    /**
     *
     */
    @Override
    public void onExit() {
        // Code here happens when exiting the level selector
    }

    /**
     *
     */
    @Override
    public void update() {
        InputCommand input = InputUtility.getTriggered();
        switch (input) {
            case MOVE_UP -> handleMoveUp();
            case MOVE_DOWN -> handleMoveDown();
            case MOVE_LEFT -> handleMoveLeft();
            case MOVE_RIGHT -> handleMoveRight();
            case TRIGGER -> handleTrigger();
        }
    }

    private void handleTrigger() {
        // TODO (MAP) : handle level selection with GameController.getInstance().playLevel(....); RETURN IF NOT POSSIBLE.
        GameController.getInstance().playLevel("map/1_JAVA_IS_YOU.csv");
        Music.play("sound/music/Pixel_Quest_MainTheme.wav");
        Music.play("sound/SFX/startStage.wav");
    }

    private void handleMoveRight() {
        // TODO (MAP) : move cursor right if possible. RETURN IF NOT POSSIBLE.

        Music.play("sound/SFX/select.wav");
    }

    private void handleMoveLeft() {
        // TODO (MAP) : move cursor left if possible. RETURN IF NOT POSSIBLE.

        Music.play("sound/SFX/select.wav");
    }

    private void handleMoveDown() {
        // TODO (MAP) : move cursor down if possible. RETURN IF NOT POSSIBLE.

        Music.play("sound/SFX/select.wav");
    }

    private void handleMoveUp() {
        // TODO (MAP) : move cursor up if possible. RETURN IF NOT POSSIBLE.

        Music.play("sound/SFX/select.wav");
    }

    /**
     *
     */
    @Override
    public void render(GraphicsContext gc) {
        // Paints the level selector map
        // Consider the current cursor position

        gc.setFill(javafx.scene.paint.Color.rgb(0, 100, 100, 0.5));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
}
