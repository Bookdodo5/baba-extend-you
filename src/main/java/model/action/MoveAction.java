package model.action;

import model.entity.Entity;
import model.map.LevelMap;

public class MoveAction implements Action {

    private final LevelMap levelMap;
    private final Entity entity;
    private final int endX, endY;
    private int startX, startY;

    public MoveAction(LevelMap levelMap, Entity entity, int endX, int endY) {
        this.levelMap = levelMap;
        this.entity = entity;
        this.endX = endX;
        this.endY = endY;
        startX = entity.getPosX();
        startY = entity.getPosY();
    }

    @Override
    public void execute() {
        startX = entity.getPosX();
        startY = entity.getPosY();
        levelMap.moveEntity(entity, endX, endY);
    }

    @Override
    public void undo() {
        levelMap.moveEntity(entity, startX, startY);
    }
}
