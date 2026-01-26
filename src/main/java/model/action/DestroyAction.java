package model.action;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;

public class DestroyAction implements Action {

    private final LevelMap levelMap;
    private final Entity entity;
    private final int posX;
    private final int posY;

    public DestroyAction(LevelMap levelMap, Entity entity) {
        this.levelMap = levelMap;
        this.entity = entity;
        this.posX = levelMap.getEntityX(entity);
        this.posY = levelMap.getEntityY(entity);
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public void execute() {
        levelMap.removeEntity(entity);
    }

    @Override
    public void undo() {
        levelMap.setEntityPosition(entity, posX, posY);
    }
}