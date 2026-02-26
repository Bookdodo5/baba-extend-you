package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import model.map.LevelLoader;
import model.map.LevelMap;
import state.*;

import java.util.*;

import static application.Constant.WIN_DELAY_MILLIS;

/**
 * A singleton class that manages the overall state of the game.
 * <ul>
 *     <li>Game states and transitions</li>
 *     <li>Communication between logic and graphic</li>
 *     <li>Game progressions</li>
 *     <li>etc. (there can be more)</li>
 * </ul>
 */
public class GameController {
    private static GameController instance;
    private final StackPane rootPane = new StackPane();
    private final Map<GameStateEnum, GameState> stateMap;
    private GameState currentState;
    private GameStateEnum currentStateEnum;

    private Color colorTheme;

    private String currentLevelFilePath;
    private final Set<String> completedLevels;
    private boolean hasPlayerWon;
    private boolean isWinSequenceActive;
    private long winSequenceStartTime;

    private GameController() {
        stateMap = new HashMap<>();
        completedLevels = new HashSet<>();
        currentLevelFilePath = null;
        colorTheme = Color.DARKSLATEGRAY;
        stateMap.put(GameStateEnum.PLAYING, new PlayingState());
        stateMap.put(GameStateEnum.MAP, new MapState());
        stateMap.put(GameStateEnum.TITLE, new TitleState());
        stateMap.put(GameStateEnum.PAUSED, new PauseState());
        stateMap.put(GameStateEnum.CREDITS, new CreditsState());
    }

    /** Returns the root JavaFX pane that contains all game UI layers. */
    public StackPane getRootPane() {
        return rootPane;
    }

    /** Returns the current color theme used for UI tinting. */
    public Color getColorTheme() {
        return colorTheme;
    }

    /**
     * Sets the color theme used for UI tinting.
     *
     * @param color the new color theme
     */
    public void setColorTheme(Color color) {
        colorTheme = color;
    }

    /**
     * Returns the singleton instance of {@code GameController}, creating it if necessary.
     *
     * @return the singleton instance
     */
    public static GameController getInstance() {
        if(instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    /**
     * Transitions the game to the specified state, calling lifecycle hooks on the old and new states.
     *
     * @param newState the state to transition to
     */
    public void setState(GameStateEnum newState) {
        GameStateEnum previousStateEnum = currentStateEnum;
        if(currentState != null) {
            currentState.onExit();
        }
        currentState = stateMap.get(newState);
        currentStateEnum = newState;
        if(currentState != null) {
            currentState.onEnter(previousStateEnum);
        }
        if(newState != GameStateEnum.PAUSED && newState != GameStateEnum.PLAYING) {
            resetCurrentLevel();
        }
    }

    /**
     * Returns the {@link GameState} registered for the given state enum.
     *
     * @param stateEnum the state enum key
     * @return the corresponding game state object
     */
    public GameState getGameState(GameStateEnum stateEnum) {
        return stateMap.get(stateEnum);
    }

    /** Resets the current level file path and win flag. */
    private void resetCurrentLevel() {
        currentLevelFilePath = null;
        hasPlayerWon = false;
    }

    /**
     * Loads the given level and transitions to the {@link GameStateEnum#PLAYING} state.
     *
     * @param levelFilePath the classpath-relative path to the level CSV file
     */
    public void playLevel(String levelFilePath) {
        PlayingState playingState = (PlayingState) getGameState(GameStateEnum.PLAYING);
        LevelMap levelMap = LevelLoader.loadLevel(levelFilePath);
        if(levelMap == null) {
            System.err.println("Failed to load level: " + levelFilePath);
            return;
        }
        playingState.loadLevel(levelMap);
        setState(GameStateEnum.PLAYING);
        currentLevelFilePath = levelFilePath;
    }

    /**
     * Returns whether the given level has been completed by the player.
     *
     * @param levelFilePath the classpath-relative path to the level CSV file
     * @return {@code true} if the level has been completed
     */
    public boolean isLevelCompleted(String levelFilePath) {
        return completedLevels.contains(levelFilePath);
    }

    /**
     * Returns the file path of the level currently being played.
     *
     * @return the current level file path, or {@code null} if no level is active
     */
    public String getCurrentLevelFilePath() {
        return currentLevelFilePath;
    }

    /** Delegates the update tick to the current game state. */
    public void update() {
        if(currentState != null) {
            currentState.update();
        }
    }

    /**
     * Sets whether the player has won the current level.
     *
     * @param isWin {@code true} if the player has won
     */
    public void setHasPlayerWon(boolean isWin) {
        this.hasPlayerWon = isWin;
    }

    /**
     * Processes the win sequence after a player wins, including the delay before returning to the map.
     *
     * @return {@code true} if a win sequence is active or was just triggered, {@code false} otherwise
     */
    public boolean processWin() {
        if (!hasPlayerWon) {
            return false;
        }

        if (!isWinSequenceActive) {
            winSequenceStartTime = System.currentTimeMillis();
            isWinSequenceActive = true;
            Audio.playSfx("sound/SFX/win.wav");

            PlayingState playingState = (PlayingState) getGameState(GameStateEnum.PLAYING);
            if (playingState != null) {
                playingState.getLevelController().addWinParticle(playingState);
            }
            return true;
        }

        if (System.currentTimeMillis() - winSequenceStartTime >= WIN_DELAY_MILLIS) {
            completedLevels.add(currentLevelFilePath);
            setState(GameStateEnum.MAP);
            resetCurrentLevel();
            Audio.playSfx("sound/SFX/confirm.wav");
            isWinSequenceActive = false;
        }

        return true;
    }

    /**
     * Delegates rendering to the current game state.
     *
     * @param gc the graphics context to render on
     */
    public void render(GraphicsContext gc) {
        if(currentState == null) {
            return;
        }
        currentState.render(gc);
    }
}
