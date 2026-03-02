package model.action;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;

/**
 * Action to create a new entity at a specified position on the level map.
 */
public class CreateAction implements Action {

    private final LevelMap levelMap;
    private final Entity entity;
    private final int posX;
    private final int posY;

    /**
     * Creates a new entity of the given type at the specified position, facing DOWN.
     *
     * @param levelMap   the level map to create the entity on
     * @param entityType the type of entity to create
     * @param posX       the x-coordinate
     * @param posY       the y-coordinate
     */
    public CreateAction(LevelMap levelMap, EntityType entityType, int posX, int posY) {
        this(levelMap, entityType, Direction.DOWN, posX, posY);
    }

    /**
     * Creates a new entity of the given type at the specified position with a custom facing direction.
     *
     * @param levelMap   the level map to create the entity on
     * @param entityType the type of entity to create
     * @param direction  the initial facing direction of the entity
     * @param posX       the x-coordinate
     * @param posY       the y-coordinate
     */
    public CreateAction(LevelMap levelMap, EntityType entityType, Direction direction, int posX, int posY) {
        this.levelMap = levelMap;
        this.posX = posX;
        this.posY = posY;
        this.entity = new Entity(entityType);
        entity.setDirection(direction);
    }

    @Override
    public void execute() {
        levelMap.setPosition(entity, posX, posY);
    }

    @Override
    public void undo() {
        levelMap.removeEntity(entity);
    }
}
