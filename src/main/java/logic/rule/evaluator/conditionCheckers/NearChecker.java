package logic.rule.evaluator.conditionCheckers;

import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Ruleset;

import java.util.List;

public class NearChecker implements ConditionChecker {
    @Override
    public boolean isSatisfied(Entity entity, Condition condition, LevelMap levelMap, Ruleset ruleset) {
        int entityX = levelMap.getEntityX(entity);
        int entityY = levelMap.getEntityY(entity);
        EntityType targetNear = condition.getParameter();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int checkX = entityX + dx;
                int checkY = entityY + dy;
                if (!levelMap.isInside(checkX, checkY)) continue;
                List<Entity> entitiesToCheck = levelMap.getEntitiesAt(checkX, checkY);
                boolean match = entitiesToCheck.stream()
                        .anyMatch(e -> e.getType() == targetNear && e != entity);
                if (match) {
                    return true;
                }
            }
        }
        return false;
    }
}
