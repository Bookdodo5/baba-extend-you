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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleEvaluatorTest {
    private RuleEvaluator evaluator;
    private LevelMap levelMap;
    private Ruleset ruleset;
    private Entity javaEntity;
    private Entity rockEntity;
    private Entity flagEntity;

    @BeforeEach
    void setUp() {
        evaluator = new RuleEvaluator();
        levelMap = new LevelMap(10, 10);
        ruleset = new Ruleset();

        javaEntity = new Entity(TypeRegistry.JAVA, 5, 5);
        rockEntity = new Entity(TypeRegistry.PAPER, 6, 5);
        flagEntity = new Entity(TypeRegistry.FLAG, 7, 5);

        javaEntity.setDirection(Direction.RIGHT);
        rockEntity.setDirection(Direction.RIGHT);
        flagEntity.setDirection(Direction.RIGHT);

        levelMap.addEntity(javaEntity);
        levelMap.addEntity(rockEntity);
        levelMap.addEntity(flagEntity);
    }

    private Rule createSimpleRule(EntityType subjectType, EntityType propertyType) {
        Entity subject = new Entity(subjectType, 0, 0);
        Entity is = new Entity(TypeRegistry.IS, 1, 0);
        Entity property = new Entity(propertyType, 2, 0);
        return new Rule(subject, is, property, List.of());
    }

    private Rule createRuleWithCondition(EntityType subjectType, EntityType propertyType,
                                         EntityType conditionType, EntityType conditionTarget) {
        Entity subject = new Entity(subjectType, 0, 0);
        Entity conditionEntity = new Entity(conditionType, 1, 0);
        Entity conditionTargetEntity = new Entity(conditionTarget, 2, 0);
        Entity is = new Entity(TypeRegistry.IS, 3, 0);
        Entity property = new Entity(propertyType, 4, 0);

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

        // PAPER IS PUSH
        Rule rule2 = createSimpleRule(TypeRegistry.TEXT_PAPER, TypeRegistry.PUSH);

        ruleset.addRules(List.of(rule1, rule2));

        assertTrue(evaluator.hasProperty(javaEntity, TypeRegistry.YOU, levelMap, ruleset));
        assertTrue(evaluator.hasProperty(rockEntity, TypeRegistry.PUSH, levelMap, ruleset));
        assertFalse(evaluator.hasProperty(javaEntity, TypeRegistry.PUSH, levelMap, ruleset));
        assertFalse(evaluator.hasProperty(rockEntity, TypeRegistry.YOU, levelMap, ruleset));
    }

    @Test
    void testHasPropertyWithCondition() {
        // JAVA ON PAPER IS YOU
        Rule rule = createRuleWithCondition(TypeRegistry.TEXT_JAVA, TypeRegistry.YOU,
                                           TypeRegistry.ON, TypeRegistry.TEXT_PAPER);
        ruleset.addRules(List.of(rule));

        // Java is not on rock
        assertFalse(evaluator.hasProperty(javaEntity, TypeRegistry.YOU, levelMap, ruleset));

        // Java is on rock
        levelMap.setEntityPosition(rockEntity, 5, 5);
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
        ruleset.addRules(List.of(rule));

        List<Entity> entities = evaluator.getEntitiesWithProperty(TypeRegistry.YOU, levelMap, ruleset);
        assertEquals(1, entities.size());
        assertTrue(entities.contains(javaEntity));
    }

    @Test
    void testGetEntitiesWithPropertyMultiple() {
        // JAVA IS PUSH
        Rule rule1 = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.PUSH);

        // PAPER IS PUSH
        Rule rule2 = createSimpleRule(TypeRegistry.TEXT_PAPER, TypeRegistry.PUSH);

        ruleset.addRules(List.of(rule1, rule2));

        List<Entity> entities = evaluator.getEntitiesWithProperty(TypeRegistry.PUSH, levelMap, ruleset);
        assertEquals(2, entities.size());
        assertTrue(entities.contains(javaEntity));
        assertTrue(entities.contains(rockEntity));
    }

    @Test
    void testGetEntitiesWithPropertyWithCondition() {
        // JAVA FACING PAPER IS WIN
        Rule rule = createRuleWithCondition(TypeRegistry.TEXT_JAVA, TypeRegistry.WIN,
                                           TypeRegistry.FACING, TypeRegistry.TEXT_PAPER);
        ruleset.addRules(List.of(rule));

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
        // JAVA IS PAPER
        Rule rule = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.TEXT_PAPER);
        ruleset.addRules(List.of(rule));

        List<Transformation> transformations = evaluator.getTransformations(levelMap, ruleset);
        assertEquals(1, transformations.size());
        assertEquals(javaEntity, transformations.getFirst().getSource());
        assertEquals(TypeRegistry.PAPER, transformations.getFirst().getTargetType());
    }

    @Test
    void testGetTransformationsMultiple() {
        // JAVA IS FLAG
        Rule rule1 = createSimpleRule(TypeRegistry.TEXT_JAVA, TypeRegistry.TEXT_FLAG);

        // PAPER IS FLAG
        Rule rule2 = createSimpleRule(TypeRegistry.TEXT_PAPER, TypeRegistry.TEXT_FLAG);

        ruleset.addRules(List.of(rule1, rule2));

        List<Transformation> transformations = evaluator.getTransformations(levelMap, ruleset);
        assertEquals(2, transformations.size());

        // Check that both java and rock should transform to flag
        List<Entity> sources = transformations.stream().map(Transformation::getSource).toList();
        assertTrue(sources.contains(javaEntity));
        assertTrue(sources.contains(rockEntity));
        transformations.forEach(t -> assertEquals(TypeRegistry.FLAG, t.getTargetType()));
    }

    @Test
    void testGetTransformationsWithCondition() {
        // JAVA ON PAPER IS FLAG
        Rule rule = createRuleWithCondition(TypeRegistry.TEXT_JAVA, TypeRegistry.TEXT_FLAG,
                                           TypeRegistry.ON, TypeRegistry.TEXT_PAPER);
        ruleset.addRules(List.of(rule));

        // Java is not on rock
        List<Transformation> transformations = evaluator.getTransformations(levelMap, ruleset);
        assertTrue(transformations.isEmpty());

        // Java is on rock
        levelMap.setEntityPosition(rockEntity, 5, 5);
        transformations = evaluator.getTransformations(levelMap, ruleset);
        assertEquals(1, transformations.size());
        assertEquals(javaEntity, transformations.getFirst().getSource());
        assertEquals(TypeRegistry.FLAG, transformations.getFirst().getTargetType());
    }
}
