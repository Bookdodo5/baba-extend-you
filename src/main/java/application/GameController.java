package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import model.map.LevelLoader;
import model.map.LevelMap;
import model.particle.Particle;
import state.*;

import java.util.*;

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
    private final Set<String> completedLevels;
    private GameState currentState;
    private GameStateEnum currentStateEnum;
    private boolean isCurrentLevelWin;
    private String currentLevelFilePath;
    private Color colorTheme;

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

    public StackPane getRootPane() {
        return rootPane;
    }

    public Color getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(Color color) {
        colorTheme = color;
    }

    public static GameController getInstance() {
        if(instance == null) {
            instance = new GameController();
        }
        return instance;
    }

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

    public GameState getGameState(GameStateEnum stateEnum) {
        return stateMap.get(stateEnum);
    }

    private void resetCurrentLevel() {
        currentLevelFilePath = null;
        isCurrentLevelWin = false;
    }

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

    public boolean isLevelCompleted(String levelFilePath) {
        return completedLevels.contains(levelFilePath);
    }

    public String getCurrentLevelFilePath() {
        return currentLevelFilePath;
    }

    public void update() {
        if(currentState != null) {
            currentState.update();
        }
    }

    public void setCurrentLevelWin(boolean isWin) {
        this.isCurrentLevelWin = isWin;
    }

    public boolean processWin() {
        if(isCurrentLevelWin) {
            completedLevels.add(currentLevelFilePath);
            setState(GameStateEnum.MAP);
            resetCurrentLevel();
            //TODO (SOUND) : play win sound;

            return true;
        }
        return false;
    }

    public void render(GraphicsContext gc) {
        if(currentState == null) {
            return;
        }
        currentState.render(gc);
    }
}
