package model.action;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;

public class CreateAction implements Action {

    private final LevelMap levelMap;
    private final Entity entity;
    private final int posX;
    private final int posY;

    public CreateAction(LevelMap levelMap, EntityType entityType, int posX, int posY) {
        this.levelMap = levelMap;
        this.entity = new Entity(entityType);
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void execute() {
        levelMap.setEntityPosition(entity, posX, posY);
    }

    @Override
    public void undo() {
        levelMap.removeEntity(entity);
    }
}
