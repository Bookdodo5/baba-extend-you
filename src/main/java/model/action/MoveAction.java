package model.action;

import model.entity.Direction;
import model.entity.Entity;
import model.map.LevelMap;

public class MoveAction implements Action {

    private final LevelMap levelMap;
    private final Entity entity;
    private final int endX, endY;
    private int startX, startY;
    private final Direction endDirection;
    private Direction startDirection;

    public MoveAction(LevelMap levelMap, Entity entity, Direction direction) {
        this.levelMap = levelMap;
        this.entity = entity;
        this.startDirection = entity.getDirection();
        this.endDirection = direction;
        this.endX = levelMap.getEntityX(entity) + direction.dx;
        this.endY = levelMap.getEntityY(entity) + direction.dy;
        startX = levelMap.getEntityX(entity);
        startY = levelMap.getEntityY(entity);
    }

    @Override
    public void execute() {
        startX = levelMap.getEntityX(entity);
        startY = levelMap.getEntityY(entity);
        startDirection = entity.getDirection();
        levelMap.setEntityPosition(entity, endX, endY);
        entity.setDirection(endDirection);
    }

    @Override
    public void undo() {
        levelMap.setEntityPosition(entity, startX, startY);
        entity.setDirection(startDirection);
    }
}
