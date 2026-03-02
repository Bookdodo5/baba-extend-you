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

    /**
     * Returns the condition type (e.g. ON, NEAR, FACING, INSTANCEOF).
     *
     * @return the condition type
     */
    public ConditionType getCondition() {
        return condition;
    }

    /**
     * Returns the entity type that serves as the parameter for this condition.
     *
     * @return the parameter entity type
     */
    public EntityType getParameter() {
        return parameter;
    }

    /**
     * Returns the text entity representing the condition word on the map.
     *
     * @return the condition text entity
     */
    public Entity getConditionText() {
        return conditionText;
    }

    /**
     * Returns the text entity representing the parameter noun on the map.
     *
     * @return the parameter text entity
     */
    public Entity getParameterText() {
        return parameterText;
    }
}
