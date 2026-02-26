package model.rule;

import model.entity.Entity;
import model.entity.EntityType;

/**
 * Represents a transformation rule that converts an entity of one type to another type.
 */
public class Transformation {
    private final Entity source;
    private final EntityType targetType;

    public Transformation(Entity source, EntityType targetType) {
        this.source = source;
        this.targetType = targetType;
    }

    /**
     * Returns the entity that will be transformed.
     *
     * @return the source entity
     */
    public Entity getSource() {
        return source;
    }

    /**
     * Returns the entity type that the source entity will be transformed into.
     *
     * @return the target entity type
     */
    public EntityType getTargetType() {
        return targetType;
    }
}
