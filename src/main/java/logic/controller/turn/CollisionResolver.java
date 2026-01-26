package logic.controller.turn;

import logic.rule.evaluator.RuleEvaluator;
import model.action.CompositeAction;
import model.action.MoveAction;
import model.action.RotateAction;
import model.entity.Direction;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Ruleset;

import java.util.*;

public class CollisionResolver {

    public CompositeAction resolveCollisions(List<MoveIntent> intents, LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator) {
        CompositeAction action = new CompositeAction();

        for (Direction direction : Direction.values()) {
            List<MoveIntent> intentsInDirection = getIntentsInDirection(intents, direction, levelMap);

            for (MoveIntent intent : intentsInDirection) {
                processIntent(intent, action, levelMap, ruleEvaluator, ruleset);
            }
        }

        return action;
    }

    private List<MoveIntent> getIntentsInDirection(List<MoveIntent> intents, Direction direction, LevelMap levelMap) {
        return intents.stream()
                .filter(intent -> intent.getDirection() == direction)
                .sorted(Comparator.comparingInt(intent -> {
                    Entity entity = intent.getEntity();
                    return switch (direction) {
                        case UP -> levelMap.getEntityY(entity);
                        case DOWN -> -levelMap.getEntityY(entity);
                        case LEFT -> levelMap.getEntityX(entity);
                        case RIGHT -> -levelMap.getEntityX(entity);
                    };
                }))
                .toList();
    }

    private void processIntent(MoveIntent intent, CompositeAction action, LevelMap levelMap, RuleEvaluator ruleEvaluator, Ruleset ruleset) {
        Entity entity = intent.getEntity();
        Direction direction = intent.getDirection();
        int targetX = levelMap.getEntityX(entity) + direction.dx;
        int targetY = levelMap.getEntityY(entity) + direction.dy;

        if (!levelMap.isInside(targetX, targetY)) {
            handleStop(intent, action, levelMap, ruleEvaluator, ruleset);
            return;
        }

        if (tryPush(intent, action, levelMap, ruleEvaluator, ruleset)) {
            action.add(new MoveAction(levelMap, entity, direction));
        } else {
            handleStop(intent, action, levelMap, ruleEvaluator, ruleset);
        }
    }

    private void handleStop(MoveIntent intent, CompositeAction action, LevelMap levelMap, RuleEvaluator ruleEvaluator, Ruleset ruleset) {
        if (intent.isFromMove()) {
            Entity entity = intent.getEntity();
            Direction direction = intent.getDirection();
            action.add(new RotateAction(entity, direction.getOpposite()));

            MoveIntent bounceIntent = new MoveIntent(entity, direction.getOpposite(), true);
            if (tryPush(bounceIntent, action, levelMap, ruleEvaluator, ruleset)) {
                action.add(new MoveAction(levelMap, entity, direction.getOpposite()));
            }
        }
    }

    private boolean tryPush(MoveIntent intent, CompositeAction action, LevelMap levelMap, RuleEvaluator ruleEvaluator, Ruleset ruleset) {
        Entity entity = intent.getEntity();
        Direction direction = intent.getDirection();
        int targetX = levelMap.getEntityX(entity) + direction.dx;
        int targetY = levelMap.getEntityY(entity) + direction.dy;

        if (!levelMap.isInside(targetX, targetY)) {
            return false;
        }

        List<Entity> pushEntities = ruleEvaluator.getEntitiesWithPropertyAt(TypeRegistry.PUSH, levelMap, ruleset, targetX, targetY);
        List<Entity> stopEntities = ruleEvaluator.getEntitiesWithPropertyAt(TypeRegistry.STOP, levelMap, ruleset, targetX, targetY);

        // If there's any STOP entity that's not also PUSH, we cannot push
        boolean isBlocked = stopEntities.stream().anyMatch(stop -> !pushEntities.contains(stop));
        if (isBlocked) {
            return false;
        }

        // If there are no PUSH entities, the push succeeds.
        if (pushEntities.isEmpty()) {
            return true;
        }

        // Check the next step in the push direction with one of the push entities
        MoveIntent pushIntent = new MoveIntent(pushEntities.getFirst(), direction, false);
        if (!tryPush(pushIntent, action, levelMap, ruleEvaluator, ruleset)) {
            return false;
        }

        for (Entity pushEntity : pushEntities) {
            action.add(new MoveAction(levelMap, pushEntity, direction));
        }

        return true;
    }
}
