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

    private void processTransformation(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        ruleEvaluator.getTransformations(levelMap, ruleset).forEach(transformation -> {
            Entity source = transformation.getSource();
            EntityType targetType = transformation.getTargetType();
            action.add(new TransformAction(levelMap, source, targetType));
        });
    }

    private void processMore(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Entity> entities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.MORE, levelMap, ruleset);
        Set<Point> occupiedPositions = new HashSet<>();
        for (Entity entity : entities) {
            Point position = levelMap.getPosition(entity);
            for(Direction direction : Direction.values()) {
                int adjacentX = position.x + direction.dx;
                int adjacentY = position.y + direction.dy;
                if(occupiedPositions.contains(new Point(adjacentX, adjacentY))) {
                    continue;
                }
                if(!levelMap.isInside(adjacentX, adjacentY)) {
                    continue;
                }
                if(ruleEvaluator.hasEntityWithPropertyAt(TypeRegistry.PUSH, levelMap, ruleset, adjacentX, adjacentY)) {
                    continue;
                }
                if(ruleEvaluator.hasEntityWithPropertyAt(TypeRegistry.STOP, levelMap, ruleset, adjacentX, adjacentY)) {
                    continue;
                }
                if(levelMap.getEntitiesAt(adjacentX, adjacentY).stream().anyMatch(e -> e.getType() == entity.getType())) {
                    continue;
                }
                CreateAction createAction = new CreateAction(levelMap, entity.getType(), adjacentX, adjacentY);
                action.add(createAction);
                occupiedPositions.add(new Point(adjacentX, adjacentY));
            }
        }
    }

    private void processYouDefeat(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Entity> youEntities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.YOU, levelMap, ruleset);
        for (Entity youEntity : youEntities) {
            int x = levelMap.getX(youEntity);
            int y = levelMap.getY(youEntity);
            if (ruleEvaluator.hasEntityWithPropertyAt(TypeRegistry.DEFEAT, levelMap, ruleset, x, y)) {
                action.add(new DestroyAction(levelMap, youEntity));
            }
        }
    }

    private void processHotMelt(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Entity> meltEntities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.MELT, levelMap, ruleset);
        for (Entity meltEntity : meltEntities) {
            int x = levelMap.getX(meltEntity);
            int y = levelMap.getY(meltEntity);
            if (ruleEvaluator.hasEntityWithPropertyAt(TypeRegistry.HOT, levelMap, ruleset, x, y)) {
                action.add(new DestroyAction(levelMap, meltEntity));
            }
        }
    }

    private void processSink(LevelMap levelMap, Ruleset ruleset, RuleEvaluator ruleEvaluator, CompositeAction action) {
        List<Entity> sinkEntities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.SINK, levelMap, ruleset);
        Set<String> processedPositions = new HashSet<>();

        for (Entity sinkEntity : sinkEntities) {
            int x = levelMap.getX(sinkEntity);
            int y = levelMap.getY(sinkEntity);
            String positionKey = x + "," + y;

            if (processedPositions.contains(positionKey)) {
                continue;
            }

            List<Entity> entitiesAtPosition = levelMap.getEntitiesAt(x, y);
            if (entitiesAtPosition.size() > 1) {
                for (Entity entity : entitiesAtPosition) {
                    action.add(new DestroyAction(levelMap, entity));
                }
                processedPositions.add(positionKey);
            }
        }
    }

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
