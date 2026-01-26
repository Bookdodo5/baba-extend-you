package logic.rule.evaluator.conditionCheckers;

import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Ruleset;

import java.util.List;

public class OnChecker implements ConditionChecker {
    @Override
    public boolean isSatisfied(Entity entity, Condition condition, LevelMap levelMap, Ruleset ruleset) {
        int checkX = levelMap.getEntityX(entity);
        int checkY = levelMap.getEntityY(entity);
        EntityType targetOn = condition.getParameter();
        List<Entity> entitiesToCheck = levelMap.getEntitiesAt(checkX, checkY);
        return entitiesToCheck.stream()
                .anyMatch(e -> e.getType() == targetOn && e != entity);
    }
}
