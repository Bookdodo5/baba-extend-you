package model.action;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;

public class CreateAction implements Action {

    private final LevelMap levelMap;
    private final Entity entity;

    public CreateAction(LevelMap levelMap, EntityType entityType, int posX, int posY) {
        this.levelMap = levelMap;
        this.entity = new Entity(entityType, posX, posY, Direction.DOWN);
    }

    @Override
    public void execute() {
        levelMap.addEntity(entity);
    }

    @Override
    public void undo() {
        levelMap.removeEntity(entity);
    }
}
