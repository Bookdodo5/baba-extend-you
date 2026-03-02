package model.entity;

import java.util.UUID;

/**
 * Represents any entity in the game, which is anything than exist in a level map: objects, words, etc.
 */
public class Entity {

    private final UUID entityId;
    private final EntityType entityType;
    private Direction direction;

    /**
     * Creates a new entity with a unique ID of the given type, facing DOWN by default.
     *
     * @param entityType the type of this entity
     */
    public Entity(EntityType entityType) {
        this.entityId = UUID.randomUUID();
        this.entityType = entityType;
        this.direction = Direction.DOWN;
    }

    /**
     * Creates a shallow copy of the given entity, sharing the same UUID and type.
     *
     * @param other the entity to copy
     */
    public Entity(Entity other) {
        this.entityId = other.entityId;
        this.entityType = other.entityType;
        this.direction = other.direction;
    }

    /**
     * Returns this entity's unique identifier.
     *
     * @return the UUID of this entity
     */
    public UUID getEntityId() {
        return entityId;
    }

    /**
     * Returns the type of this entity.
     *
     * @return the entity type
     */
    public EntityType getType() {
        return entityType;
    }

    /**
     * Returns the current facing direction of this entity.
     *
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Sets the facing direction of this entity.
     *
     * @param direction the new direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Entity other && this.entityId.equals(other.entityId);
    }

    @Override
    public int hashCode() {
        return entityId.hashCode();
    }
}