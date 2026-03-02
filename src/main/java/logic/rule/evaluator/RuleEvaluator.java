package logic.rule.evaluator;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.entity.word.PropertyType;
import model.map.LevelMap;
import model.rule.Rule;
import model.rule.Ruleset;

import java.awt.*;
import java.util.List;

import model.rule.Transformation;

/**
 * A class responsible for handling different types of query related to rules and properties of entities.
 */
public class RuleEvaluator {
    private final ConditionEvaluator conditionEvaluator;
    private final InheritanceResolver inheritanceResolver;

    public RuleEvaluator() {
        conditionEvaluator = new ConditionEvaluator();
        inheritanceResolver = new InheritanceResolver();
    }

    /**
     * Returns {@code true} if the entity satisfies the subject and conditions of the given rule.
     *
     * @param entity   the entity to test
     * @param rule     the rule whose subject and conditions are evaluated
     * @param levelMap the current level map
     * @param ruleset  the active ruleset
     * @return {@code true} if the entity matches the rule's subject and all its conditions
     */
    public boolean hasPropertyFromRule(Entity entity, Rule rule, LevelMap levelMap, Ruleset ruleset) {
        boolean isSubject = inheritanceResolver.isInstanceOf(entity, rule.getSubject(), levelMap, ruleset);
        boolean conditionsMet = conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap, ruleset);
        return isSubject && conditionsMet;
    }

    /**
     * Returns {@code true} if the entity has the given property according to the current ruleset.
     * Text entities are always considered to have PUSH.
     *
     * @param entity   the entity to check
     * @param property the property type to look for
     * @param levelMap the current level map
     * @param ruleset  the active ruleset
     * @return {@code true} if any rule grants the property to this entity
     */
    public boolean hasProperty(Entity entity, PropertyType property, LevelMap levelMap, Ruleset ruleset) {
        // All text entities are inherently PUSH
        if (property == TypeRegistry.PUSH && entity.getType().isText()) {
            return true;
        }

        return ruleset.getRules().stream()
                .filter(rule -> rule.getEffect() == property)
                .anyMatch(rule -> hasPropertyFromRule(entity, rule, levelMap, ruleset));
    }

    /**
     * Returns all entities on the map that currently have the given property.
     *
     * @param property the property to filter by
     * @param levelMap the current level map
     * @param ruleset  the active ruleset
     * @return list of entities with the property
     */
    public List<Entity> getEntitiesWithProperty(PropertyType property, LevelMap levelMap, Ruleset ruleset) {
        return levelMap.getEntities().stream()
                .filter(entity -> hasProperty(entity, property, levelMap, ruleset))
                .toList();
    }

    /**
     * Returns all entities at the given map position that have the given property.
     *
     * @param property the property to filter by
     * @param levelMap the current level map
     * @param ruleset  the active ruleset
     * @param position the grid position to check
     * @return list of entities at that position with the property
     */
    public List<Entity> getEntitiesWithPropertyAt(PropertyType property, LevelMap levelMap, Ruleset ruleset, Point position) {
        return getEntitiesWithProperty(property, levelMap, ruleset).stream()
                .filter(entity -> position.equals(levelMap.getPosition(entity)))
                .toList();
    }

    /**
     * Returns {@code true} if at least one entity at the given position has the specified property.
     *
     * @param property the property to check
     * @param levelMap the current level map
     * @param ruleset  the active ruleset
     * @param position the grid position to check
     * @return {@code true} if any entity at the position has the property
     */
    public boolean hasEntityWithPropertyAt(PropertyType property, LevelMap levelMap, Ruleset ruleset, Point position) {
        return levelMap.getEntitiesAt(position).stream()
                .anyMatch(entity -> hasProperty(entity, property, levelMap, ruleset));
    }

    /**
     * Returns all "X IS Y" transformations that should occur this frame (where Y is not a property).
     * Excludes entities with the "X IS X" rule blocking the transformation.
     *
     * @param levelMap the current level map
     * @param ruleset  the active ruleset
     * @return list of {@link Transformation} objects describing each entity-to-type change
     */
    public List<Transformation> getTransformations(LevelMap levelMap, Ruleset ruleset) {
        List<Entity> XisXEntities = ruleset.getRules().stream()
                .filter(rule -> !(rule.getEffect() instanceof PropertyType))
                .filter(rule -> rule.getVerb() == TypeRegistry.IS)
                .flatMap(rule -> levelMap.getEntities().stream()
                        .filter(entity -> inheritanceResolver.isInstanceOf(entity, rule.getSubject(), levelMap, ruleset))
                        .filter(entity -> conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap, ruleset))
                        .filter(entity -> entity.getType() == rule.getEffect()))
                .toList();

        return ruleset.getRules().stream()
                .filter(rule -> !(rule.getEffect() instanceof PropertyType))
                .filter(rule -> rule.getVerb() == TypeRegistry.IS)
                .flatMap(rule -> {
                    EntityType targetType = rule.getEffect();
                    return levelMap.getEntities().stream()
                            .filter(entity -> inheritanceResolver.isInstanceOf(entity, rule.getSubject(), levelMap, ruleset))
                            .filter(entity -> conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap, ruleset))
                            .filter(entity -> !XisXEntities.contains(entity))
                            .map(entity -> new Transformation(entity, targetType));
                })
                .toList();
    }

    /**
     * Returns all "X HAS Y" spawning transformations for the current frame.
     *
     * @param levelMap the current level map
     * @param ruleset  the active ruleset
     * @return list of {@link Transformation} objects describing each HAS spawn
     */
    public List<Transformation> getHasTransformations(LevelMap levelMap, Ruleset ruleset) {
        return ruleset.getRules().stream()
                .filter(rule -> rule.getVerb() == TypeRegistry.HAS)
                .flatMap(rule -> {
                    EntityType targetType = rule.getEffect();
                    return levelMap.getEntities().stream()
                            .filter(entity -> inheritanceResolver.isInstanceOf(entity, rule.getSubject(), levelMap, ruleset))
                            .filter(entity -> conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap, ruleset))
                            .map(entity -> new Transformation(entity, targetType));
                })
                .toList();
    }

    /**
     * Returns the map positions where the win condition is currently met
     * (a WIN entity shares a cell with a YOU entity).
     *
     * @param levelMap the current level map
     * @param ruleset  the active ruleset
     * @return list of positions where the win condition is met
     */
    public List<Point> getWinConditionMetPositions(LevelMap levelMap, Ruleset ruleset) {
        return levelMap.getEntities().stream()
                .filter(entity -> hasProperty(entity, TypeRegistry.WIN, levelMap, ruleset))
                .filter(entity -> hasEntityWithPropertyAt(
                        TypeRegistry.YOU,
                        levelMap, ruleset,
                        levelMap.getPosition(entity))
                )
                .map(levelMap::getPosition)
                .toList();
    }
}