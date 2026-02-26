package logic.rule.evaluator.conditionCheckers;

import logic.rule.evaluator.InheritanceResolver;
import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Ruleset;

import java.util.List;

/** Checks for "A FACING B IS X" conditions. */
public class FacingChecker implements ConditionChecker {
    /** {@inheritDoc} Satisfied when the entity is facing a cell occupied by the condition's parameter type. */
    @Override
    public boolean isSatisfied(Entity entity, Condition condition, LevelMap levelMap, Ruleset ruleset) {
        InheritanceResolver inheritanceResolver = new InheritanceResolver();

        EntityType targetFacing = condition.getParameter();
        Direction facing = entity.getDirection();
        int checkX = levelMap.getX(entity) + facing.dx;
        int checkY = levelMap.getY(entity) + facing.dy;
        List<Entity> entitiesToCheck = levelMap.getEntitiesAt(checkX, checkY);
        return entitiesToCheck.stream()
                .anyMatch(e -> inheritanceResolver.isInstanceOf(e, targetFacing, levelMap, ruleset) && e != entity);
    }
}
