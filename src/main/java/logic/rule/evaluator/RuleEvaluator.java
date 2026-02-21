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

    public boolean hasPropertyFromRule(Entity entity, Rule rule, LevelMap levelMap, Ruleset ruleset) {
        boolean isSubject = inheritanceResolver.isInstanceOf(entity, rule.getSubject(), levelMap, ruleset);
        boolean conditionsMet = conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap, ruleset);
        return isSubject && conditionsMet;
    }

    public boolean hasProperty(Entity entity, PropertyType property, LevelMap levelMap, Ruleset ruleset) {
        // All text entities are inherently PUSH
        if (property == TypeRegistry.PUSH && entity.getType().isText()) {
            return true;
        }

        return ruleset.getRules().stream()
                .filter(rule -> rule.getEffect() == property)
                .anyMatch(rule -> hasPropertyFromRule(entity, rule, levelMap, ruleset));
    }

    public List<Entity> getEntitiesWithProperty(PropertyType property, LevelMap levelMap, Ruleset ruleset) {
        return levelMap.getEntities().stream()
                .filter(entity -> hasProperty(entity, property, levelMap, ruleset))
                .toList();
    }

    public List<Entity> getEntitiesWithPropertyAt(PropertyType property, LevelMap levelMap, Ruleset ruleset, Point position) {
        return getEntitiesWithProperty(property, levelMap, ruleset).stream()
                .filter(entity -> position.equals(levelMap.getPosition(entity)))
                .toList();
    }

    public boolean hasEntityWithPropertyAt(PropertyType property, LevelMap levelMap, Ruleset ruleset, Point position) {
        return levelMap.getEntitiesAt(position).stream()
                .anyMatch(entity -> hasProperty(entity, property, levelMap, ruleset));
    }

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