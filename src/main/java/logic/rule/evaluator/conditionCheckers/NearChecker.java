package logic.rule.evaluator.conditionCheckers;

import logic.rule.evaluator.InheritanceResolver;
import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Ruleset;

import java.util.List;

/** Checks for "A NEAR B IS X" conditions. */
public class NearChecker implements ConditionChecker {
    /** {@inheritDoc} Satisfied when the entity is within 1 cell (including diagonals) of the condition's parameter type. */
    @Override
    public boolean isSatisfied(Entity entity, Condition condition, LevelMap levelMap, Ruleset ruleset) {
        InheritanceResolver inheritanceResolver = new InheritanceResolver();

        int entityX = levelMap.getX(entity);
        int entityY = levelMap.getY(entity);
        EntityType targetNear = condition.getParameter();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int checkX = entityX + dx;
                int checkY = entityY + dy;
                if (!levelMap.isInside(checkX, checkY)) continue;
                List<Entity> entitiesToCheck = levelMap.getEntitiesAt(checkX, checkY);
                boolean match = entitiesToCheck.stream()
                        .anyMatch(e -> inheritanceResolver.isInstanceOf(e, targetNear, levelMap, ruleset) && e != entity);
                if (match) {
                    return true;
                }
            }
        }
        return false;
    }
}
