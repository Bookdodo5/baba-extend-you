package logic.rule;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.word.NounType;
import model.entity.word.PartOfSpeech;
import model.entity.word.PropertyType;
import model.map.LevelMap;
import model.rule.Ruleset;

import java.util.List;
import model.rule.Transformation;

public class RuleEvaluator {
    private final Ruleset ruleset;
    private final ConditionEvaluator conditionEvaluator;

    public RuleEvaluator(Ruleset ruleset) {
        this.ruleset = ruleset;
        conditionEvaluator = new ConditionEvaluator();
    }

    public boolean hasProperty(Entity entity, PropertyType property, LevelMap levelMap) {
        return ruleset.getRules().stream()
                .filter(rule -> rule.getEffect() == property)
                .filter(rule -> rule.getSubject() == entity.getType())
                .anyMatch(rule -> conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap));
    }

    public List<Entity> getEntitiesWithProperty(PropertyType property, LevelMap levelMap) {
        return levelMap.getEntities().stream()
                .filter(entity -> hasProperty(entity, property, levelMap))
                .toList();
    }

    public List<Transformation> getTransform(LevelMap levelMap) {
        return ruleset.getRules().stream()
                .filter(rule -> rule.getEffect().getPartOfSpeech() == PartOfSpeech.NOUN)
                .flatMap(rule -> {
                    NounType nounEffect = (NounType)rule.getEffect();
                    EntityType targetType = nounEffect.getReferencedType();
                    return levelMap.getEntities().stream()
                            .filter(entity -> rule.getSubject() == entity.getType())
                            .filter(entity -> conditionEvaluator.evaluate(entity, rule.getConditions(), levelMap))
                            .map(entity -> new Transformation(entity, targetType));
                })
                .toList();
    }
}