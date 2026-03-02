package state;

import application.Audio;
import javafx.scene.canvas.GraphicsContext;
import application.GameController;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import logic.input.InputCommand;
import logic.level.LevelController;
import logic.input.InputUtility;
import model.entity.Entity;
import model.map.LevelMap;
import model.particle.Particle;
import utils.GraphicUtils;
import utils.ImageUtils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static application.Constant.*;

/**
 * Represents the playing state where the player interacts with the level.
 */
public class PlayingState implements GameState {

    private final LevelController levelController;
    private final List<Particle> particles;

    private static final ColorAdjust INACTIVE_TEXT_EFFECT = new ColorAdjust();
    static {
        INACTIVE_TEXT_EFFECT.setSaturation(-0.5);
        INACTIVE_TEXT_EFFECT.setBrightness(-0.33);
    }

    /**
     * Loads a level map into the level controller, replacing any previously loaded level.
     *
     * @param levelMap the level map to load
     */
    public void loadLevel(LevelMap levelMap) {
        levelController.setLevelMap(levelMap);
    }

    /**
     * Adds a particle effect to be rendered this frame.
     *
     * @param particle the particle to add
     */
    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    /**
     * Returns the level controller managing the current level.
     *
     * @return the level controller
     */
    public LevelController getLevelController() {
        return levelController;
    }

    public PlayingState() {
        levelController = new LevelController();
        particles = new ArrayList<>();
    }

    /** {@inheritDoc} No setup is needed when entering the playing state. */
    @Override
    public void onEnter(GameStateEnum previousState) {
    }

    /** {@inheritDoc} No cleanup is needed when exiting the playing state. */
    @Override
    public void onExit() {

    }

    /** {@inheritDoc} Updates particles, processes win, runs level logic, and handles menu input. */
    @Override
    public void update() {
        particles.removeIf(Particle::isDead);
        particles.forEach(Particle::update);

        if (GameController.getInstance().processWin()) {
            return; // Controls disabled during win animation
        }

        levelController.update(this);

        InputCommand playerInput = InputUtility.getTriggered();
        if (playerInput == InputCommand.MENU) {
            Audio.playSfx("sound/SFX/esc.wav");
            GameController.getInstance().setState(GameStateEnum.PAUSED);
        }

        if(levelController.getLevelMap().getEntities().size() >= MAX_ENTITY_LIMIT) {
            System.err.println("Entity limit reached: " + levelController.getLevelMap().getEntities().size());
            System.err.println("The level is too complex. Return to map...");
            GameController.getInstance().setState(GameStateEnum.MAP);
        }
    }

    /**
     * Renders the game state including background, entities, and particles.
     *
     * @param gc the graphics context to render on
     */
    @Override
    public void render(GraphicsContext gc) {
        int xOffset = (int) (gc.getCanvas().getWidth() / 2) - (levelController.getLevelMap().getWidth() * SPRITE_SIZE) / 2;
        int yOffset = (int) (gc.getCanvas().getHeight() / 2) - (levelController.getLevelMap().getHeight() * SPRITE_SIZE) / 2;
        Point offset = new Point(xOffset, yOffset);

        LevelMap levelMap = levelController.getLevelMap();
        GraphicUtils.renderBackground(
                gc, offset,
                SPRITE_SIZE * levelMap.getWidth(),
                SPRITE_SIZE * levelMap.getHeight()
        );

        renderEntities(gc, offset);
        renderParticles(gc, offset);
    }

    /**
     * Renders all entities on the level map in z-index order.
     * Inactive text entities are rendered with a desaturated/dimmed effect.
     *
     * @param gc     the graphics context
     * @param offset the pixel offset to center the level on the canvas
     */
    private void renderEntities(GraphicsContext gc, Point offset) {
        Set<Entity> activeTexts = levelController.getRuleset().getActiveTexts();
        Set<Entity> inactiveTexts = levelController.getLevelMap().getEntities().stream()
                .filter(e -> e.getType().isText() && !activeTexts.contains(e))
                .collect(Collectors.toSet());

        GraphicUtils.renderEntities(
                gc, levelController.getLevelMap(), offset,
                inactiveTexts, INACTIVE_TEXT_EFFECT
        );
    }

    /**
     * Renders all active particles on the canvas.
     *
     * @param gc     the graphics context
     * @param offset the pixel offset to center the level on the canvas
     */
    private void renderParticles(GraphicsContext gc, Point offset) {
        for (Particle particle : particles) {

            Image particleImage = particle.getImage();
            gc.drawImage(
                    particleImage,
                    SPRITE_SIZE * particle.getCurrentFrame(), 0,
                    SPRITE_SIZE, SPRITE_SIZE,
                    SPRITE_SIZE * particle.getX() + offset.x,
                    SPRITE_SIZE * particle.getY() + offset.y,
                    SPRITE_SIZE, SPRITE_SIZE
            );
        }
    }
}
