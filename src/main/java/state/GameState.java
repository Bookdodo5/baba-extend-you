package state;

import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a game state (e.g. title screen, playing, paused) with lifecycle hooks for entering,
 * exiting, updating, and rendering.
 */
public interface GameState {
    void onEnter(GameStateEnum previousState);
    void onExit();
    void update();
    void render(GraphicsContext gc);
}
