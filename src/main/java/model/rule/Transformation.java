package model.rule;

import model.entity.Entity;
import model.entity.EntityType;

public class Transformation {
    private final Entity source;
    private final EntityType targetType;

    public Transformation(Entity source, EntityType targetType) {
        this.source = source;
        this.targetType = targetType;
    }

    public Entity getSource() {
        return source;
    }

    public EntityType getTargetType() {
        return targetType;
    }
}
