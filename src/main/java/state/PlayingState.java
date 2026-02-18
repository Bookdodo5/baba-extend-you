package state;

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

        Color theme = GameController.getInstance().getColorTheme();
        Color bgColor = theme.interpolate(Color.BLACK, 0.8).darker();
        gc.setFill(bgColor);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        renderEntities(gc);
        renderParticles(gc);

        gc.setFill(theme.interpolate(Color.TRANSPARENT, 0.9));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private void renderEntities(GraphicsContext gc) {

        long currentTime = System.currentTimeMillis();
        long totalCycleMs = MILLISECONDS_PER_FRAME * WOBBLE_FRAME_COUNT;
        int frameInCycle = (int) (currentTime % totalCycleMs);
        int animationFrameNumber = frameInCycle / MILLISECONDS_PER_FRAME;

        LevelMap levelMap = levelController.getLevelMap();
        Set<Entity> activeEntities = levelController.getRuleset().getActiveEntities();

        List<Entity> entities = levelMap.getEntities().stream()
                .sorted(Comparator.comparingInt(e -> e.getType().getZIndex()))
                .toList();

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
