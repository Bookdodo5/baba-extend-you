package model.entity;

import java.util.UUID;

public class Entity {

    private final UUID entityId;
    private final EntityType entityType;
    private Direction direction;

    public Entity(EntityType entityType) {
        this.entityId = UUID.randomUUID();
        this.entityType = entityType;
        this.direction = Direction.DOWN;
    }

    public Entity(Entity other) {
        this.entityId = other.entityId;
        this.entityType = other.entityType;
        this.direction = other.direction;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public EntityType getType() {
        return entityType;
    }


    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}