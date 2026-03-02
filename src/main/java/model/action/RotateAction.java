package model.action;

import model.entity.Direction;
import model.entity.Entity;

/**
 * An action that rotates an entity to a new direction.
 */
public class RotateAction implements Action{

    private final Direction startDirection;
    private final Direction endDirection;
    private final Entity entity;

    /**
     * Creates a rotate action for the given entity.
     *
     * @param entity       the entity to rotate
     * @param endDirection the target direction after rotation
     */
    public RotateAction(Entity entity, Direction endDirection) {
        this.startDirection = entity.getDirection();
        this.endDirection = endDirection;
        this.entity = entity;
    }

    @Override
    public void execute() {
        entity.setDirection(endDirection);
    }

    @Override
    public void undo() {
        entity.setDirection(startDirection);
    }
}
