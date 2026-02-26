package model.action;

import model.entity.Direction;
import model.entity.Entity;
import model.map.LevelMap;
import model.particle.Particle;
import model.particle.ParticleType;
import state.PlayingState;
import utils.ImageUtils;

import java.awt.*;

/**
 * An action that moves an entity in a specified direction on the level map.
 */
public class MoveAction implements Action {

    private final LevelMap levelMap;
    private final Entity entity;
    private final int endX, endY;
    private int startX, startY;
    private final Direction endDirection;
    private Direction startDirection;

    /**
     * Creates a move action from a specified starting position.
     *
     * @param levelMap  the level map
     * @param entity    the entity to move
     * @param fromPos   the starting position
     * @param direction the direction to move in
     */
    public MoveAction(LevelMap levelMap, Entity entity, Point fromPos, Direction direction) {
        this.levelMap = levelMap;
        this.entity = entity;
        this.startDirection = entity.getDirection();
        this.endDirection = direction;
        this.endX = fromPos.x + direction.dx;
        this.endY = fromPos.y + direction.dy;
        startX = fromPos.x;
        startY = fromPos.y;
    }

    /**
     * Creates a move action using the entity's current position on the map as the starting position.
     *
     * @param levelMap  the level map
     * @param entity    the entity to move
     * @param direction the direction to move in
     */
    public MoveAction(LevelMap levelMap, Entity entity, Direction direction) {
        this(levelMap, entity, levelMap.getPosition(entity), direction);
    }

    @Override
    public void execute() {
        startX = levelMap.getX(entity);
        startY = levelMap.getY(entity);
        startDirection = entity.getDirection();
        levelMap.setPosition(entity, endX, endY);
        entity.setDirection(endDirection);
    }

    @Override
    public void undo() {
        levelMap.setPosition(entity, startX, startY);
        entity.setDirection(startDirection);
    }

    /**
     * Spawns a puff particle effect at the entity's starting position.
     *
     * @param playingState the playing state to add particles to
     */
    public void addParticle(PlayingState playingState) {
        playingState.addParticle(new Particle(
                startX + (Math.random() - 0.5) / 2.0,
                startY + (Math.random() - 0.5) / 2.0,
                (startX - endX) / 1000.0, (startY - endY) / 1000.0,
                ParticleType.PUFF,
                ImageUtils.averageColor(entity.getType().getSpriteSheet())
        ));
    }
}
