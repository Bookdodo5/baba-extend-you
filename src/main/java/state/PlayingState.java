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
import model.entity.EntityType;
import model.map.LevelMap;
import model.particle.Particle;
import utils.ImageUtils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static application.Constant.*;

/**
 * Represents the playing state where the player interacts with the level.
 */
public class PlayingState implements GameState {

    private final LevelController levelController;
    private final List<Particle> particles;

    private static final Point[] SURROUNDING_DIRECTIONS = {
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0)
    };

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

        Color theme = GameController.getInstance().getColorTheme();

        renderBackground(gc, theme, offset);
        renderEntities(gc, offset);
        renderParticles(gc, offset);

        gc.setFill(theme.interpolate(Color.TRANSPARENT, 0.9));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    /**
     * Renders the background for the level area.
     *
     * @param gc     the graphics context
     * @param theme  the current color theme
     * @param offset the pixel offset to center the level on the canvas
     */
    private void renderBackground(GraphicsContext gc, Color theme, Point offset) {
        Color outerBgColor = theme.interpolate(Color.BLACK, 0.8);
        gc.setFill(outerBgColor);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        Color innerBgColor = outerBgColor.darker().darker();
        int innerWidth = SPRITE_SIZE * levelController.getLevelMap().getWidth();
        int innerHeight = SPRITE_SIZE * levelController.getLevelMap().getHeight();
        gc.setFill(innerBgColor);
        gc.fillRect(offset.x, offset.y, innerWidth, innerHeight);
    }

    /**
     * Renders all entities on the level map in z-index order.
     *
     * @param gc     the graphics context
     * @param offset the pixel offset to center the level on the canvas
     */
    private void renderEntities(GraphicsContext gc, Point offset) {

        long currentTime = System.currentTimeMillis();
        int animationFrame = (int) ((currentTime / MILLISECONDS_PER_FRAME) % WOBBLE_FRAME_COUNT);

        LevelMap levelMap = levelController.getLevelMap();
        Set<Entity> activeTexts = levelController.getRuleset().getActiveTexts();

        List<Entity> entities = levelMap.getEntities().stream()
                .sorted(Comparator.comparingInt(e -> e.getType().getZIndex()))
                .toList();

        for (Entity entity : entities) {
            EntityType entityType = entity.getType();
            Image image = entityType.getSpriteSheet();

            int gridX = levelMap.getX(entity);
            int gridY = levelMap.getY(entity);
            int drawX = SPRITE_SIZE * gridX + offset.x;
            int drawY = SPRITE_SIZE * gridY + offset.y;

            int spriteRow = switch (entityType.getAnimationStyle()) {
                case WOBBLE -> 0;
                case TILED -> getSurroundingNumber(entity, levelMap);
                case DIRECTIONAL -> getDirectionalNumber(entity);
            };

            if (entityType.isText() && !activeTexts.contains(entity)) {
                gc.setEffect(INACTIVE_TEXT_EFFECT);
            }
            ImageUtils.drawSprite(gc, image, animationFrame, spriteRow, drawX, drawY);
            gc.setEffect(null);
        }
    }

    /**
     * Returns the tiling row index for an entity based on which of its four cardinal neighbors
     * share the same type (used for TILED animation style).
     *
     * @param entity   the entity to compute the surrounding number for
     * @param levelMap the current level map
     * @return a bitmask (0–15) representing which cardinal neighbors match
     */
    private int getSurroundingNumber(Entity entity, LevelMap levelMap) {
        int surroundingNumber = 0;
        for (int direction = 0; direction < 4; direction++) {
            List<Entity> surroundingEntities = levelMap.getEntitiesAt(
                    levelMap.getX(entity) + SURROUNDING_DIRECTIONS[direction].x,
                    levelMap.getY(entity) + SURROUNDING_DIRECTIONS[direction].y
            );
            boolean hasSurroundingInDirection = surroundingEntities.stream()
                    .anyMatch(e -> e.getType() == entity.getType());
            if (hasSurroundingInDirection) {
                surroundingNumber += (1 << direction);
            }
        }
        return surroundingNumber;
    }

    /**
     * Returns the sprite row index for an entity based on its current facing direction
     * (used for DIRECTIONAL animation style).
     *
     * @param entity the entity whose direction determines the row
     * @return the sprite row index
     */
    private int getDirectionalNumber(Entity entity) {
        return switch (entity.getDirection()) {
            case UP -> 0;
            case RIGHT -> 1;
            case LEFT -> 2;
            case DOWN -> 3;
        };
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
