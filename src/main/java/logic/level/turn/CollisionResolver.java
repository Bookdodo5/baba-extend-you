package logic.level.turn;

import logic.rule.evaluator.RuleEvaluator;
import model.action.CompositeAction;
import model.action.MoveAction;
import model.action.RotateAction;
import model.entity.Direction;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Ruleset;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Resolves collisions (PUSH, STOP) for a list of MoveIntents to produce a final CompositeAction.
 */
public class CollisionResolver {

    /**
     * Resolves all movement intents against PUSH and STOP rules and returns the resulting actions.
     *
     * @param intents       the list of move intents to resolve
     * @param levelMap      the real level map
     * @param ruleset       the active ruleset
     * @param ruleEvaluator used to query entity properties
     * @return a {@link CompositeAction} of all resolved moves
     */
    public CompositeAction resolveCollisions(List<MoveIntent> intents, LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator) {
        CompositeAction action = new CompositeAction();

        for (Direction direction : Direction.values()) {
            List<MoveIntent> intentsInDirection = getIntentsInDirection(intents, direction, levelMap);
            LevelMap workingMap = new LevelMap(levelMap);

            for (MoveIntent intent : intentsInDirection) {
                processIntent(intent, action, workingMap, levelMap, ruleEvaluator, ruleset);
            }
        }

        return action;
    }

    /**
     * Returns intents moving in the specified direction, sorted so entities are processed in the correct order.
     *
     * @param intents   all move intents
     * @param direction the direction to filter by
     * @param levelMap  used to read entity positions
     * @return sorted list of intents in the given direction
     */
    private List<MoveIntent> getIntentsInDirection(List<MoveIntent> intents, Direction direction, LevelMap levelMap) {
        return intents.stream()
                .filter(intent -> intent.getDirection() == direction)
                .sorted(Comparator.comparingInt(intent -> {
                    Entity entity = intent.getEntity();
                    return switch (direction) {
                        case UP -> levelMap.getY(entity);
                        case DOWN -> -levelMap.getY(entity);
                        case LEFT -> levelMap.getX(entity);
                        case RIGHT -> -levelMap.getX(entity);
                    };
                }))
                .toList();
    }

    /**
     *  Process a single move intent
     *
     * @param intent        the intent to process
     * @param action        the composite action to append to
     * @param workingMap    the map to track entity positions during processing
     * @param levelMap      the real level map
     * @param ruleEvaluator used to query entity properties
     * @param ruleset       the active ruleset
     */
    private void processIntent(MoveIntent intent, CompositeAction action, LevelMap workingMap, LevelMap levelMap, RuleEvaluator ruleEvaluator, Ruleset ruleset) {
        Entity entity = intent.getEntity();
        Direction direction = intent.getDirection();
        if(intent.isFromMove()) {
            direction = workingMap.getEntityById(entity.getEntityId()).getDirection();
        }

        int targetX = workingMap.getX(entity) + direction.dx;
        int targetY = workingMap.getY(entity) + direction.dy;

        if (!workingMap.isInside(targetX, targetY)) {
            handleStop(intent, action, workingMap, levelMap, ruleEvaluator, ruleset);
            return;
        }

        if (tryPush(intent, action, workingMap, levelMap, ruleEvaluator, ruleset)) {
            action.add(new MoveAction(levelMap, entity, workingMap.getPosition(entity), direction));
            workingMap.setPosition(entity, targetX, targetY);
        } else {
            handleStop(intent, action, workingMap, levelMap, ruleEvaluator, ruleset);
        }
    }

    /**
     * Handles the case where an entity is stopped: rotates autonomous MOVE entities and
     * attempts a bounce in the opposite direction.
     *
     * @param intent        the intent that was stopped
     * @param action        the composite action to append to
     * @param workingMap    the simulation map
     * @param levelMap      the real level map
     * @param ruleEvaluator used to query entity properties
     * @param ruleset       the active ruleset
     */
    private void handleStop(MoveIntent intent, CompositeAction action, LevelMap workingMap, LevelMap levelMap, RuleEvaluator ruleEvaluator, Ruleset ruleset) {
        Entity entity = intent.getEntity();
        Direction direction = intent.getDirection();
        if (intent.isFromMove()) {
            Entity workingEntity = workingMap.getEntityById(entity.getEntityId());
            direction = workingEntity.getDirection();
            action.add(new RotateAction(entity, direction.getOpposite()));

            MoveIntent bounceIntent = new MoveIntent(entity, direction.getOpposite(), false);
            if (tryPush(bounceIntent, action, workingMap, levelMap, ruleEvaluator, ruleset)) {
                action.add(new MoveAction(levelMap, entity, workingMap.getPosition(entity), direction.getOpposite()));
                int targetX = workingMap.getX(entity) + direction.getOpposite().dx;
                int targetY = workingMap.getY(entity) + direction.getOpposite().dy;
                workingMap.setPosition(workingEntity, targetX, targetY);
                workingEntity.setDirection(direction.getOpposite());
            }
        }
        else if(direction != entity.getDirection()) {
            action.add(new RotateAction(entity, direction));
        }
    }

    /**
     * Recursively attempts to push the entities blocking the path of the given intent.
     *
     * @param intent        the intent requesting a push
     * @param action        the composite action to append push moves to
     * @param workingMap    the simulation map
     * @param levelMap      the real level map
     * @param ruleEvaluator used to query entity properties
     * @param ruleset       the active ruleset
     * @return {@code true} if the push succeeded (path is clear), {@code false} if blocked
     */
    private boolean tryPush(MoveIntent intent, CompositeAction action, LevelMap workingMap, LevelMap levelMap, RuleEvaluator ruleEvaluator, Ruleset ruleset) {
        Entity entity = intent.getEntity();
        Direction direction = intent.getDirection();

        if(intent.isFromMove()) {
            direction = workingMap.getEntityById(entity.getEntityId()).getDirection();
        }

        int targetX = workingMap.getX(entity) + direction.dx;
        int targetY = workingMap.getY(entity) + direction.dy;
        Point target = new Point(targetX, targetY);

        if (!levelMap.isInside(targetX, targetY)) {
            return false;
        }

        List<Entity> pushEntities = ruleEvaluator.getEntitiesWithPropertyAt(TypeRegistry.PUSH, workingMap, ruleset, target);
        List<Entity> stopEntities = ruleEvaluator.getEntitiesWithPropertyAt(TypeRegistry.STOP, workingMap, ruleset, target);

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
        if (!tryPush(pushIntent, action, workingMap, levelMap, ruleEvaluator, ruleset)) {
            return false;
        }

        for (Entity pushEntity : pushEntities) {
            Entity trueEntity = levelMap.getEntityById(pushEntity.getEntityId());
            action.add(new MoveAction(levelMap, trueEntity, workingMap.getPosition(pushEntity), direction));
            workingMap.setPosition(pushEntity, targetX + direction.dx, targetY + direction.dy);
        }

        return true;
    }
}
