package model.action;

import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;

public class TransformAction implements Action {

    private final LevelMap levelMap;
    private final Entity oldEntity;
    private final Entity newEntity;
    private final int posX;
    private final int posY;

    public TransformAction(LevelMap levelMap, Entity entity, EntityType entityType) {
        this.levelMap = levelMap;
        this.oldEntity = entity;
        this.newEntity = new Entity(entityType);
        newEntity.setDirection(oldEntity.getDirection());
        this.posX = levelMap.getX(entity);
        this.posY = levelMap.getY(entity);
    }

    @Override
    public void execute() {
        levelMap.removeEntity(oldEntity);
        levelMap.setPosition(newEntity, posX, posY);
    }

    @Override
    public void undo() {
        levelMap.removeEntity(newEntity);
        levelMap.setPosition(oldEntity, posX, posY);
    }
}
