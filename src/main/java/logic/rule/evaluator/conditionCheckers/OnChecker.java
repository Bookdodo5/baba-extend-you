package logic.rule.evaluator.conditionCheckers;

import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Ruleset;

import java.util.List;

/** Checks for "A ON B IS X" conditions. */
public class OnChecker implements ConditionChecker {
    /** {@inheritDoc} Satisfied when the entity shares its cell with the condition's parameter type. */
    @Override
    public boolean isSatisfied(Entity entity, Condition condition, LevelMap levelMap, Ruleset ruleset) {
        int checkX = levelMap.getX(entity);
        int checkY = levelMap.getY(entity);
        EntityType targetOn = condition.getParameter();
        List<Entity> entitiesToCheck = levelMap.getEntitiesAt(checkX, checkY);
        return entitiesToCheck.stream()
                .anyMatch(e -> e.getType() == targetOn && e != entity);
    }
}
