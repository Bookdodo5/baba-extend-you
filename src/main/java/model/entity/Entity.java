package model.entity;

import java.util.UUID;

public class Entity {

    private final UUID entityId;
    private final EntityType entityType;
    private int posX, posY;
    private Direction direction;

    public Entity(EntityType entityType, int posX, int posY) {
        this.entityId = UUID.randomUUID();
        this.entityType = entityType;
        this.posX = posX;
        this.posY = posY;
        this.direction = Direction.DOWN;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public EntityType getType() {
        return entityType;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}