package state;

import javafx.scene.canvas.GraphicsContext;
import application.GameController;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import logic.input.InputCommand;
import logic.level.LevelController;
import logic.input.InputUtility;
import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;
import model.particle.Particle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static application.Constant.*;

public class PlayingState implements GameState {

    private final LevelController levelController;
    private final List<Particle> particles;

    public void loadLevel(LevelMap levelMap) {
        levelController.setLevelMap(levelMap);
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public LevelController getLevelController() {
        return levelController;
    }

    public PlayingState() {
        levelController = new LevelController();
        particles = new ArrayList<>();
    }

    /**
     *
     */
    @Override
    public void onEnter(GameStateEnum previousState) {

    }

    /**
     *
     */
    @Override
    public void onExit() {

    }

    /**
     *
     */
    @Override
    public void update() {
        boolean success = GameController.getInstance().processWin();
        if (success) {
            return;
        }

        levelController.update(this);

        particles.removeIf(Particle::isDead);
        particles.forEach(Particle::update);

        InputCommand playerInput = InputUtility.getTriggered();
        if (playerInput == InputCommand.MENU) {
            GameController.getInstance().setState(GameStateEnum.PAUSED);
        }
    }

    /**
     * Renders the game state including background, entities, and particles.
     *
     * @param gc the graphics context to render on
     */
    @Override
    public void render(GraphicsContext gc) {

        //gc.setFill(Color.rgb(20, 25, 30));
        gc.setFill(Color.rgb(40, 35, 49));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        renderEntities(gc);
        renderParticles(gc);
    }

    private void renderEntities(GraphicsContext gc) {

        Point[] surroundingDirections = {new Point(0,-1),new Point(1,0), new Point(0,1),new Point(-1,0)};

        long currentTime = System.currentTimeMillis();
        long totalCycleMs = MILLISECONDS_PER_FRAME * WOBBLE_FRAME_COUNT;
        int frameInCycle = (int) (currentTime % totalCycleMs);
        int animationFrameNumber = frameInCycle / MILLISECONDS_PER_FRAME;
        int mapWidth = 0;
        int mapHeight = 0;

        LevelMap levelMap = levelController.getLevelMap();
        Set<Entity> activeEntities = levelController.getRuleset().getActiveEntities();

        List<Entity> entities = levelMap.getEntities().stream()
                .sorted(Comparator.comparingInt(e -> e.getType().getZIndex()))
                .toList();


        // TODO: Maybe I should move this somewhere else -mistertfy64 2026-02-18
        for (Entity entity : entities) {
            mapWidth = Math.max(levelMap.getX(entity)+1,mapWidth);
            mapHeight = Math.max(levelMap.getY(entity)+1,mapHeight);
        }

        for(Entity entity : entities) {
            EntityType entityType = entity.getType();
            Image image = entityType.getSpriteSheet();
            int xCoordinate = levelMap.getX(entity);
            int yCoordinate = levelMap.getY(entity);

            boolean isText = entityType.isText();
            boolean isActiveText = activeEntities.contains(entity);
            if(isText && !isActiveText) {
                ColorAdjust inactiveText = new ColorAdjust();
                inactiveText.setSaturation(-0.5);
                inactiveText.setBrightness(-0.33);
                gc.setEffect(inactiveText);
            }

            switch (entityType.getAnimationStyle()) {
                case WOBBLE -> gc.drawImage(
                        image,
                        SPRITE_SIZE * animationFrameNumber, 0,
                        SPRITE_SIZE, SPRITE_SIZE,
                        SPRITE_SIZE * xCoordinate,
                        SPRITE_SIZE * yCoordinate,
                        SPRITE_SIZE, SPRITE_SIZE
                );
                case TILED -> {
                    int surroundingNumber = 0;
                    for (int direction = 0; direction < 4; direction++){
                        List<Entity> surroundingEnemies = levelMap.getEntitiesAt(xCoordinate + surroundingDirections[direction].x,yCoordinate + surroundingDirections[direction].y);
                        boolean hasSurroundingInDirection = surroundingEnemies.stream().anyMatch(e -> e.getType() == entityType);
                        if (hasSurroundingInDirection) {
                            surroundingNumber += Math.powExact(2,direction);
                        }
                    }

                    gc.drawImage(
                            image,
                            SPRITE_SIZE * animationFrameNumber, SPRITE_SIZE * surroundingNumber,
                            SPRITE_SIZE, SPRITE_SIZE,
                            SPRITE_SIZE * xCoordinate,
                            SPRITE_SIZE * yCoordinate,
                            SPRITE_SIZE, SPRITE_SIZE
                    );
                }
                case DIRECTIONAL -> {
                    int directionalNumber = getDirectionalNumber(entity);
                    gc.drawImage(
                            image,
                            SPRITE_SIZE * animationFrameNumber, SPRITE_SIZE * directionalNumber,
                            SPRITE_SIZE, SPRITE_SIZE,
                            SPRITE_SIZE * xCoordinate,
                            SPRITE_SIZE * yCoordinate,
                            SPRITE_SIZE, SPRITE_SIZE
                    );
                }
                case null, default -> gc.drawImage(
                        image,
                        SPRITE_SIZE * animationFrameNumber, 0,
                        SPRITE_SIZE, SPRITE_SIZE,
                        SPRITE_SIZE * xCoordinate,
                        SPRITE_SIZE * yCoordinate,
                        SPRITE_SIZE, SPRITE_SIZE
                );
            }
            gc.setEffect(null);
        }
    }

    private static int getDirectionalNumber(Entity entity) {
        Direction direction = entity.getDirection();
        // simplest way I could think of...
        // -mistertfy64, 2026-02-16
        if (direction == Direction.UP) {
            return 0;
        } else if (direction == Direction.RIGHT) {
            return 1;
        } else if (direction == Direction.LEFT) {
            return 2;
        } else if (direction == Direction.DOWN) {
            return 3;
        }
        return 0;
    }

    private void renderParticles(GraphicsContext gc) {
        for (Particle particle : particles) {
            Image particleImage = particle.getImage();
            gc.drawImage(
                    particleImage,
                    SPRITE_SIZE * particle.getCurrentFrame(), 0,
                    SPRITE_SIZE, SPRITE_SIZE,
                    SPRITE_SIZE * particle.getX(),
                    SPRITE_SIZE * particle.getY(),
                    SPRITE_SIZE, SPRITE_SIZE
            );
        }
    }
}
