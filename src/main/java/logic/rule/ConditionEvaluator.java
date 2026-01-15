package logic.rule;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Condition;

import java.util.ArrayList;
import java.util.List;

public class ConditionEvaluator {
    public boolean evaluate(Entity entity, ArrayList<Condition> conditions, LevelMap levelMap) {
        for (Condition condition : conditions) {
            if (!isValid(entity, condition, levelMap)) return false;
        }
        return true;
    }

    private boolean isValid(Entity entity, Condition condition, LevelMap levelMap) {
        if (condition.getCondition() == TypeRegistry.ON) {
            int checkX = entity.getPosX();
            int checkY = entity.getPosY();
            EntityType targetOn = condition.getParameter();
            List<Entity> entitiesToCheck = levelMap.getEntitiesAt(checkX, checkY);
            return entitiesToCheck.stream()
                    .anyMatch(e -> e.getType() == targetOn && e != entity);
        }

        if (condition.getCondition() == TypeRegistry.NEAR) {
            int entityX = entity.getPosX();
            int entityY = entity.getPosY();
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

        if (condition.getCondition() == TypeRegistry.FACING) {
            EntityType targetNear = condition.getParameter();
            Direction facing = entity.getDirection();
            int checkX = entity.getPosX() + facing.getDx();
            int checkY = entity.getPosY() + facing.getDy();
            List<Entity> entitiesToCheck = levelMap.getEntitiesAt(checkX, checkY);
            return entitiesToCheck.stream()
                    .anyMatch(e -> e.getType() == targetNear && e != entity);
        }

        return true;
    }
}
