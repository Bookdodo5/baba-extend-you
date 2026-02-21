package logic.rule.evaluator;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Rule;
import model.rule.Ruleset;
import model.rule.Transformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleEvaluatorTest {
    private RuleEvaluator evaluator;
    private LevelMap levelMap;
    private Ruleset ruleset;
    private Entity javaEntity;
    private Entity paperEntity;
    private Entity flagEntity;

    @BeforeEach
    void setUp() {
        evaluator = new RuleEvaluator();
        levelMap = new LevelMap(10, 10);
        ruleset = new Ruleset();

        javaEntity = new Entity(TypeRegistry.JAVA);
        paperEntity = new Entity(TypeRegistry.DOCUMENT);
        flagEntity = new Entity(TypeRegistry.FLAG);

        javaEntity.setDirection(Direction.RIGHT);
        paperEntity.setDirection(Direction.RIGHT);
        flagEntity.setDirection(Direction.RIGHT);

        levelMap.setPosition(javaEntity, 5, 5);
        levelMap.setPosition(paperEntity, 6, 5);
        levelMap.setPosition(flagEntity, 7, 5);
    }

    private Rule createSimpleRule(EntityType subjectType, EntityType propertyType) {
        Entity subject = new Entity(subjectType);
        Entity is = new Entity(TypeRegistry.IS);
        Entity property = new Entity(propertyType);
        return new Rule(subject, is, property, List.of());
    }

    private Rule createRuleWithCondition(EntityType subjectType, EntityType propertyType,
                                         EntityType conditionType, EntityType conditionTarget) {
        Entity subject = new Entity(subjectType);
        Entity conditionEntity = new Entity(conditionType);
        Entity conditionTargetEntity = new Entity(conditionTarget);
        Entity is = new Entity(TypeRegistry.IS);
        Entity property = new Entity(propertyType);

        Condition condition = new Condition(conditionEntity, conditionTargetEntity);
        return new Rule(subject, is, property, List.of(condition));
    }

    @Test
    void testHasPropertyWithNoRules() {
        assertFalse(evaluator.hasProperty(javaEntity, TypeRegistry.YOU, levelMap, ruleset));
        assertFalse(evaluator.hasProperty(javaEntity, TypeRegistry.PUSH, levelMap, ruleset));
    }

    @Test
    void testHasPropertyWithSimpleRules() {
        // JAVA IS YOU
        Rule rule1 = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.YOU);

        // DOCUMENT IS PUSH
        Rule rule2 = createSimpleRule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.PUSH);

        ruleset.setRules(List.of(rule1, rule2));

        assertTrue(evaluator.hasProperty(javaEntity, TypeRegistry.YOU, levelMap, ruleset));
        assertTrue(evaluator.hasProperty(paperEntity, TypeRegistry.PUSH, levelMap, ruleset));
        assertFalse(evaluator.hasProperty(javaEntity, TypeRegistry.PUSH, levelMap, ruleset));
        assertFalse(evaluator.hasProperty(paperEntity, TypeRegistry.YOU, levelMap, ruleset));
    }

    @Test
    void testHasPropertyWithCondition() {
        // JAVA ON DOCUMENT IS YOU
        Rule rule = createRuleWithCondition(TypeRegistry.TEXT_JAVA, TypeRegistry.YOU,
                                           TypeRegistry.ON, TypeRegistry.TEXT_DOCUMENT);
        ruleset.setRules(List.of(rule));

        // Java is not on rock
        assertFalse(evaluator.hasProperty(javaEntity, TypeRegistry.YOU, levelMap, ruleset));

        // Java is on rock
        levelMap.setPosition(paperEntity, 5, 5);
        assertTrue(evaluator.hasProperty(javaEntity, TypeRegistry.YOU, levelMap, ruleset));
    }

    @Test
    void testGetEntitiesWithPropertyEmpty() {
        List<Entity> entities = evaluator.getEntitiesWithProperty(TypeRegistry.YOU, levelMap, ruleset);
        assertTrue(entities.isEmpty());
    }

    @Test
    void testGetEntitiesWithPropertySingle() {
        // JAVA IS YOU
        Rule rule = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.YOU);
        ruleset.setRules(List.of(rule));

        List<Entity> entities = evaluator.getEntitiesWithProperty(TypeRegistry.YOU, levelMap, ruleset);
        assertEquals(1, entities.size());
        assertTrue(entities.contains(javaEntity));
    }

    @Test
    void testGetEntitiesWithPropertyMultiple() {
        // JAVA IS PUSH
        Rule rule1 = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.PUSH);

        // DOCUMENT IS PUSH
        Rule rule2 = createSimpleRule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.PUSH);

        ruleset.setRules(List.of(rule1, rule2));

        List<Entity> entities = evaluator.getEntitiesWithProperty(TypeRegistry.PUSH, levelMap, ruleset);
        assertEquals(2, entities.size());
        assertTrue(entities.contains(javaEntity));
        assertTrue(entities.contains(paperEntity));
    }

    @Test
    void testGetEntitiesWithPropertyWithCondition() {
        // JAVA FACING DOCUMENT IS WIN
        Rule rule = createRuleWithCondition(TypeRegistry.TEXT_JAVA, TypeRegistry.WIN,
                                           TypeRegistry.FACING, TypeRegistry.TEXT_DOCUMENT);
        ruleset.setRules(List.of(rule));

        // Java is facing rock
        List<Entity> entities = evaluator.getEntitiesWithProperty(TypeRegistry.WIN, levelMap, ruleset);
        assertEquals(1, entities.size());
        assertTrue(entities.contains(javaEntity));

        // Java is not facing rock
        javaEntity.setDirection(Direction.LEFT);
        entities = evaluator.getEntitiesWithProperty(TypeRegistry.WIN, levelMap, ruleset);
        assertTrue(entities.isEmpty());
    }

    @Test
    void testGetTransformationsEmpty() {
        List<Transformation> transformations = evaluator.getTransformations(levelMap, ruleset);
        assertTrue(transformations.isEmpty());
    }

    @Test
    void testGetTransformationsSingle() {
        // JAVA IS DOCUMENT
        Rule rule = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.TEXT_DOCUMENT);
        ruleset.setRules(List.of(rule));

        List<Transformation> transformations = evaluator.getTransformations(levelMap, ruleset);
        assertEquals(1, transformations.size());
        assertEquals(javaEntity, transformations.getFirst().getSource());
        assertEquals(TypeRegistry.DOCUMENT, transformations.getFirst().getTargetType());
    }

    @Test
    void testGetTransformationsMultiple() {
        // JAVA IS FLAG
        Rule rule1 = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.TEXT_FLAG);

        // DOCUMENT IS FLAG
        Rule rule2 = createSimpleRule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.TEXT_FLAG);

        ruleset.setRules(List.of(rule1, rule2));

        List<Transformation> transformations = evaluator.getTransformations(levelMap, ruleset);
        assertEquals(2, transformations.size());

        // Check that both java and rock should transform to flag
        List<Entity> sources = transformations.stream().map(Transformation::getSource).toList();
        assertTrue(sources.contains(javaEntity));
        assertTrue(sources.contains(paperEntity));
        transformations.forEach(t -> assertEquals(TypeRegistry.FLAG, t.getTargetType()));
    }

    @Test
    void testGetTransformationsWithCondition() {
        // JAVA ON DOCUMENT IS FLAG
        Rule rule = createRuleWithCondition(TypeRegistry.TEXT_JAVA, TypeRegistry.TEXT_FLAG,
                                           TypeRegistry.ON, TypeRegistry.TEXT_DOCUMENT);
        ruleset.setRules(List.of(rule));

        // Java is not on rock
        List<Transformation> transformations = evaluator.getTransformations(levelMap, ruleset);
        assertTrue(transformations.isEmpty());

        // Java is on rock
        levelMap.setPosition(paperEntity, 5, 5);
        transformations = evaluator.getTransformations(levelMap, ruleset);
        assertEquals(1, transformations.size());
        assertEquals(javaEntity, transformations.getFirst().getSource());
        assertEquals(TypeRegistry.FLAG, transformations.getFirst().getTargetType());
    }

    @Test
    void testGetEntitiesWithPropertyAt() {
        // JAVA IS YOU
        Rule rule = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.YOU);
        ruleset.setRules(List.of(rule));

        List<Entity> entities = evaluator.getEntitiesWithPropertyAt(TypeRegistry.YOU, levelMap, ruleset,  new Point(5, 5));
        assertEquals(1, entities.size());
        assertTrue(entities.contains(javaEntity));

        entities = evaluator.getEntitiesWithPropertyAt(TypeRegistry.YOU, levelMap, ruleset,  new Point(6, 5));
        assertTrue(entities.isEmpty());
    }

    @Test
    void testHasEntitiesWithPropertyAt() {
        // JAVA IS YOU
        Rule rule = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.YOU);
        ruleset.setRules(List.of(rule));

        boolean result1 = evaluator.hasEntityWithPropertyAt(TypeRegistry.YOU, levelMap, ruleset, new Point(5, 5));
        assertTrue(result1);

        boolean result2 = evaluator.hasEntityWithPropertyAt(TypeRegistry.PUSH, levelMap, ruleset,  new Point(5, 5));
        assertFalse(result2);

        boolean result3 = evaluator.hasEntityWithPropertyAt(TypeRegistry.YOU, levelMap, ruleset,  new Point(6, 5));
        assertFalse(result3);
    }
}
