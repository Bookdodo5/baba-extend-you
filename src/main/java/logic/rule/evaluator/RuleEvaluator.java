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

    public boolean hasProperty(Entity entity, PropertyType property, LevelMap levelMap, Ruleset ruleset) {
        // All text entities are inherently PUSH
        if (property == TypeRegistry.PUSH && entity.getType().isText()) {
            return true;
        }

        return ruleset.getRules().stream()
                .filter(rule -> rule.getEffect() == property)
                .filter(rule -> inheritanceResolver.isInstanceOf(entity, rule.getSubject(), levelMap, ruleset))
                .anyMatch(rule -> conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap, ruleset));
    }

    public List<Entity> getEntitiesWithProperty(PropertyType property, LevelMap levelMap, Ruleset ruleset) {
        return levelMap.getEntities().stream()
                .filter(entity -> hasProperty(entity, property, levelMap, ruleset))
                .toList();
    }

    public List<Entity> getEntitiesWithPropertyAt(PropertyType property, LevelMap levelMap, Ruleset ruleset, int x, int y) {
        return getEntitiesWithProperty(property, levelMap, ruleset).stream()
                .filter(entity -> levelMap.getEntityX(entity) == x && levelMap.getEntityY(entity) == y)
                .toList();
    }

    public List<Entity> getEntitiesWithPropertyAt(PropertyType property, LevelMap levelMap, Ruleset ruleset, Point position) {
        return getEntitiesWithPropertyAt(property, levelMap, ruleset, position.x, position.y);
    }

    public boolean hasEntityWithPropertyAt(PropertyType property, LevelMap levelMap, Ruleset ruleset, int x, int y) {
        return levelMap.getEntitiesAt(x, y).stream()
                .anyMatch(entity -> hasProperty(entity, property, levelMap, ruleset));
    }

    public boolean hasEntityWithPropertyAt(PropertyType property, LevelMap levelMap, Ruleset ruleset, Point position) {
        return hasEntityWithPropertyAt(property, levelMap, ruleset, position.x, position.y);
    }

    public List<Transformation> getTransformations(LevelMap levelMap, Ruleset ruleset) {
        List<EntityType> XisXTypes = ruleset.getRules().stream()
                .filter(rule -> !(rule.getEffect() instanceof PropertyType))
                .filter(rule -> rule.getVerb() == TypeRegistry.IS)
                .filter(rule -> rule.getSubject() == rule.getEffect())
                .map(Rule::getSubject)
                .toList();

        return ruleset.getRules().stream()
                .filter(rule -> !(rule.getEffect() instanceof PropertyType))
                .filter(rule -> rule.getVerb() == TypeRegistry.IS)
                .filter(rule -> !XisXTypes.contains(rule.getSubject()))
                .flatMap(rule -> {
                    EntityType targetType = rule.getEffect();
                    return levelMap.getEntities().stream()
                            .filter(entity -> inheritanceResolver.isInstanceOf(entity, rule.getSubject(), levelMap, ruleset))
                            .filter(entity -> conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap, ruleset))
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

    public boolean isWinConditionMet(LevelMap levelMap, Ruleset ruleset) {
        return levelMap.getEntities().stream()
                .filter(entity -> hasProperty(entity, TypeRegistry.WIN, levelMap, ruleset))
                .anyMatch(entity -> hasEntityWithPropertyAt(
                        TypeRegistry.YOU,
                        levelMap,
                        ruleset,
                        levelMap.getEntityX(entity),
                        levelMap.getEntityY(entity))
                );
    }
}