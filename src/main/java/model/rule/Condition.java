package model.rule;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.word.ConditionType;

import java.util.UUID;

public class Condition {
    private final ConditionType condition;
    private final EntityType parameter;
    private final Entity conditionText;
    private final Entity parameterText;

    public Condition(Entity conditionText, Entity parameterText) {
        this.conditionText = conditionText;
        this.parameterText = parameterText;
        this.condition = (ConditionType)conditionText.getType();
        this.parameter = parameterText.getType();
    }

    public ConditionType getCondition() {
        return condition;
    }
    public EntityType getParameter() {
        return parameter;
    }
    public Entity getConditionText() {
        return conditionText;
    }
    public Entity getParameterText() {
        return parameterText;
    }
}
