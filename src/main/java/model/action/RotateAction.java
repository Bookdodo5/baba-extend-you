package model.action;

import model.entity.Direction;
import model.entity.Entity;

public class RotateAction implements Action{

    private final Direction startDirection;
    private final Direction endDirection;
    private final Entity entity;

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
