package logic.controller;

import model.map.LevelLoader;
import model.map.LevelMap;
import view.GameScreen;

public class GameController {
    private static GameController instance;
    private GameState currentState = GameState.TITLE;

    private LevelController levelController;
    private GameScreen gameScreen;
    private LevelMap currentMap;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public GameState getState() {
        return currentState;
    }

    public LevelMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(LevelMap levelMap) {
        this.currentMap = levelMap;
    }

    public void setLevelController(LevelController levelController) {
        this.levelController = levelController;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    private void setState(GameState newState) {
        this.currentState = newState;
    }

    public void startGame() {
        if(currentState == GameState.TITLE) {
            setState(GameState.PLAYING);
        }
    }

    public void playMap(String mapName) {
        currentMap = LevelLoader.loadLevel(mapName);
        levelController.setLevelMap(currentMap);
        if(currentState == GameState.MAP && currentMap != null) {
            setState(GameState.PLAYING);
        }
    }

    public void pauseGame() {
        if (currentState == GameState.PLAYING) {
            setState(GameState.PAUSED);
        }
    }

    public void resumeGame() {
        if (currentState == GameState.PAUSED) {
            setState(GameState.PLAYING);
        }
    }

    public void returnToTitle() {
        setState(GameState.TITLE);
    }

    public void handleWin() {
        if (currentState == GameState.PLAYING) {
            setState(GameState.WIN);
        }
    }
}
