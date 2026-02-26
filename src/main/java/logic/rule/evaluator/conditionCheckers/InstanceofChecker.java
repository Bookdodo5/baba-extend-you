package logic.rule.evaluator.conditionCheckers;

import logic.rule.evaluator.InheritanceResolver;
import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Ruleset;

/** Checks for "A INSTANCEOF B IS X" conditions. */
public class InstanceofChecker implements ConditionChecker {

    /** {@inheritDoc} Satisfied when the entity is an instance of the condition's parameter type. */
    @Override
    public boolean isSatisfied(Entity entity, Condition condition, LevelMap levelMap, Ruleset ruleset) {
        InheritanceResolver inheritanceResolver = new InheritanceResolver();
        EntityType targetType = condition.getParameter();
        return inheritanceResolver.isInstanceOf(entity, targetType, levelMap, ruleset);
    }
}
