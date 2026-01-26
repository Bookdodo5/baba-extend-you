package application;

public class GameManager {
    private static GameManager instance;
    private GameState currentState;

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public GameState getState() {
        return currentState;
    }

    public void setState(GameState newState) {
        this.currentState = newState;
    }
}
