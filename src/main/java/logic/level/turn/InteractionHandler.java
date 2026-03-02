package logic.level.turn;

import logic.rule.evaluator.RuleEvaluator;
import model.action.*;
import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Ruleset;
import model.rule.Transformation;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles interactions between entities on the level map after movement based on the current rule.
 */
public class InteractionHandler {

    /**
     * Runs all interaction passes (transformation, MORE, SINK, DEFEAT, HOT/MELT, HAS)
     * and returns a composite action containing all resulting changes.
     *
     * @param levelMap      the current level map
     * @param ruleset       the active ruleset
     * @param ruleEvaluator the rule evaluator used for property queries
     * @return a {@link CompositeAction} containing all interaction actions
     */
    public CompositeAction handleInteractions(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator) {
        CompositeAction action = new CompositeAction();
        processTransformation(levelMap, ruleset, ruleEvaluator, action);
        processMore(levelMap, ruleset, ruleEvaluator, action);
        processSink(levelMap, ruleset, ruleEvaluator, action);
        processYouDefeat(levelMap, ruleset, ruleEvaluator, action);
        processHotMelt(levelMap, ruleset, ruleEvaluator, action);
        processHas(levelMap, ruleset, ruleEvaluator, action);
        return action;
    }

    /** Applies all "X IS Y" transformation rules (where Y is not a property). */
    private void processTransformation(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        ruleEvaluator.getTransformations(levelMap, ruleset).forEach(transformation -> {
            Entity source = transformation.getSource();
            EntityType targetType = transformation.getTargetType();
            action.add(new TransformAction(levelMap, source, targetType));
        });
    }

    /** Creates copies of entities with the MORE property in each adjacent free cell. */
    private void processMore(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Entity> entities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.MORE, levelMap, ruleset);
        Set<Point> occupiedPositions = new HashSet<>();
        for (Entity entity : entities) {
            Point position = levelMap.getPosition(entity);
            for(Direction direction : Direction.values()) {
                int adjacentX = position.x + direction.dx;
                int adjacentY = position.y + direction.dy;
                Point adjacent = new Point(adjacentX, adjacentY);

                if(occupiedPositions.contains(adjacent)) {
                    continue;
                }
                if(!levelMap.isInside(adjacentX, adjacentY)) {
                    continue;
                }
                if(ruleEvaluator.hasEntityWithPropertyAt(TypeRegistry.PUSH, levelMap, ruleset, adjacent)) {
                    continue;
                }
                if(ruleEvaluator.hasEntityWithPropertyAt(TypeRegistry.STOP, levelMap, ruleset, adjacent)) {
                    continue;
                }
                if(levelMap.getEntitiesAt(adjacent).stream().anyMatch(e -> e.getType() == entity.getType())) {
                    continue;
                }
                CreateAction createAction = new CreateAction(levelMap, entity.getType(), adjacentX, adjacentY);
                action.add(createAction);
                occupiedPositions.add(adjacent);
            }
        }
    }

    /** Destroys YOU entities that occupy the same cell as a DEFEAT entity. */
    private void processYouDefeat(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Entity> youEntities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.YOU, levelMap, ruleset);
        for (Entity youEntity : youEntities) {
            Point position = levelMap.getPosition(youEntity);
            if (ruleEvaluator.hasEntityWithPropertyAt(TypeRegistry.DEFEAT, levelMap, ruleset, position)) {
                action.add(new DestroyAction(levelMap, youEntity));
            }
        }
    }

    /** Destroys MELT entities that occupy the same cell as a HOT entity. */
    private void processHotMelt(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Entity> meltEntities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.MELT, levelMap, ruleset);
        for (Entity meltEntity : meltEntities) {
            Point position = levelMap.getPosition(meltEntity);
            if (ruleEvaluator.hasEntityWithPropertyAt(TypeRegistry.HOT, levelMap, ruleset, position)) {
                action.add(new DestroyAction(levelMap, meltEntity));
            }
        }
    }

    /** Destroys all entities (including the SINK entity itself) that share a cell with a SINK entity. */
    private void processSink(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Entity> sinkEntities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.SINK, levelMap, ruleset);
        Set<Point> processedPositions = new HashSet<>();

        for (Entity sinkEntity : sinkEntities) {
            Point position = levelMap.getPosition(sinkEntity);
            if (processedPositions.contains(position)) {
                continue;
            }

            List<Entity> entitiesAtPosition = levelMap.getEntitiesAt(position);
            if (entitiesAtPosition.size() > 1) {
                for (Entity entity : entitiesAtPosition) {
                    action.add(new DestroyAction(levelMap, entity));
                }
                processedPositions.add(position);
            }
        }
    }

    /**
     * Applies "X HAS Y" rules: when an entity matching X is destroyed, a new entity of type Y
     * is created at the same position.
     */
    private void processHas(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Action> destroyActions = action.getActions().stream()
                .filter(a -> a instanceof DestroyAction)
                .toList();
        List<Transformation> hasTransformations = ruleEvaluator.getHasTransformations(levelMap, ruleset);
        for (Transformation transformation : hasTransformations) {
            Entity source = transformation.getSource();
            EntityType targetType = transformation.getTargetType();
            destroyActions.stream()
                    .filter(a -> a instanceof DestroyAction)
                    .map(a -> (DestroyAction) a)
                    .filter(a -> a.getEntity() == source)
                    .forEach(a -> {
                        Point position = levelMap.getPosition(a.getEntity());
                        action.add(new CreateAction(levelMap, targetType, source.getDirection(), position.x, position.y));
                    });
        }
    }
}
