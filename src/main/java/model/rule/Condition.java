package model.rule;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.word.ConditionType;
import model.entity.word.NounType;


/**
 * Represents a condition in a rule, consisting of a condition type and a parameter type.
 */
public class Condition {
    private final ConditionType condition;
    private final EntityType parameter;
    private final Entity conditionText;
    private final Entity parameterText;

    public Condition(Entity conditionText, Entity parameterText) {
        if(! (parameterText.getType() instanceof NounType)) {
            throw new IllegalArgumentException("Parameter text must be of type NounType");
        }
        this.conditionText = conditionText;
        this.parameterText = parameterText;
        this.condition = (ConditionType) conditionText.getType();
        this.parameter = ((NounType) parameterText.getType()).getReferencedType();
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
