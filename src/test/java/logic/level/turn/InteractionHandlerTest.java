package logic.level.turn;

import logic.rule.evaluator.RuleEvaluator;
import model.action.CompositeAction;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Rule;
import model.rule.Ruleset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InteractionHandlerTest {
    private InteractionHandler interactionHandler;
    private RuleEvaluator ruleEvaluator;
    private LevelMap levelMap;
    private final List<Rule> rules = new ArrayList<>();

    @BeforeEach
    void setUp() {
        interactionHandler = new InteractionHandler();
        ruleEvaluator = new RuleEvaluator();
        levelMap = new LevelMap(10, 10);
        rules.clear();
    }

    private Entity addEntity(EntityType type, int x, int y) {
        Entity entity = new Entity(type);
        levelMap.setPosition(entity, x, y);
        return entity;
    }

    private void rule(EntityType subject, EntityType operator, EntityType effect) {
        Entity subjectEntity = new Entity(subject);
        Entity operatorEntity = new Entity(operator);
        Entity effectEntity = new Entity(effect);
        rules.add(new Rule(subjectEntity, operatorEntity, effectEntity, List.of()));
    }

    private CompositeAction executeInteractions() {
        Ruleset ruleset = new Ruleset();
        ruleset.setRules(rules);
        return interactionHandler.handleInteractions(levelMap, ruleset, ruleEvaluator);
    }

    @Test
    void testHandleInteractionsNoRules() {
        CompositeAction result = executeInteractions();
        assertEquals(0, result.size());
    }

    @Test
    void testProcessTransformationSingle() {
        Entity java = addEntity(TypeRegistry.JAVA, 5, 5);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.TEXT_DOCUMENT);

        CompositeAction result = executeInteractions();

        assertEquals(2, result.size());
        result.execute();

        assertFalse(levelMap.getEntities().contains(java));
        assertEquals(TypeRegistry.DOCUMENT, levelMap.getEntitiesAt(5, 5).getFirst().getType());
    }

    @Test
    void testProcessTransformationMultiple() {
        Entity java = addEntity(TypeRegistry.JAVA, 5, 5);
        Entity python = addEntity(TypeRegistry.PYTHON, 7, 5);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.TEXT_FLAG);
        rule(TypeRegistry.TEXT_PYTHON, TypeRegistry.IS, TypeRegistry.TEXT_FLAG);

        CompositeAction result = executeInteractions();

        assertEquals(4, result.size());
        result.execute();

        assertFalse(levelMap.getEntities().contains(java));
        assertFalse(levelMap.getEntities().contains(python));
    }

    @Test
    void testProcessTransformationDenied() {
        addEntity(TypeRegistry.JAVA, 5, 5);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.TEXT_FLAG);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.TEXT_JAVA);

        CompositeAction result = executeInteractions();
        assertEquals(0, result.size());
    }

    @Test
    void testProcessMoreSingle() {
        Entity paper = addEntity(TypeRegistry.DOCUMENT, 3, 3);
        rule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.IS, TypeRegistry.MORE);

        CompositeAction result = executeInteractions();
        assertEquals(4, result.size());

        result.execute();
        assertEquals(5, levelMap.getEntities().size());
        assertTrue(levelMap.getEntitiesAt(3, 3).contains(paper));
        assertEquals(TypeRegistry.DOCUMENT, levelMap.getEntitiesAt(3, 4).getFirst().getType());
        assertEquals(TypeRegistry.DOCUMENT, levelMap.getEntitiesAt(3, 2).getFirst().getType());
        assertEquals(TypeRegistry.DOCUMENT, levelMap.getEntitiesAt(2, 3).getFirst().getType());
        assertEquals(TypeRegistry.DOCUMENT, levelMap.getEntitiesAt(4, 3).getFirst().getType());
    }

    @Test
    void testProcessMoreWithBlockers() {
        addEntity(TypeRegistry.DOCUMENT, 3, 3);
        addEntity(TypeRegistry.DOCUMENT, 3, 4);
        addEntity(TypeRegistry.WIRE, 3, 2);
        addEntity(TypeRegistry.JAVA, 2, 3);
        addEntity(TypeRegistry.ERROR, 4, 3);
        rule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.IS, TypeRegistry.MORE);
        rule(TypeRegistry.TEXT_WIRE, TypeRegistry.IS, TypeRegistry.STOP);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.PUSH);
        rule(TypeRegistry.TEXT_ERROR, TypeRegistry.IS, TypeRegistry.DEFEAT); // Should not matter

        CompositeAction result = executeInteractions();
        assertEquals(4, result.size());

        result.execute();
        assertEquals(9, levelMap.getEntities().size());
        assertEquals(2, levelMap.getEntitiesAt(4, 3).size()); // error + new paper
        assertEquals(1, levelMap.getEntitiesAt(3, 4).size()); // paper2 blocking more
        assertEquals(1, levelMap.getEntitiesAt(3, 2).size()); // stop blocking more
        assertEquals(1, levelMap.getEntitiesAt(2, 3).size()); // push blocking more
    }

    @Test
    void testProcessMoreMultiple() {
        addEntity(TypeRegistry.DOCUMENT, 3, 3);
        addEntity(TypeRegistry.JAVA, 2, 3);
        rule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.IS, TypeRegistry.MORE);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.MORE);

        CompositeAction result = executeInteractions();
        assertEquals(8, result.size());

        result.execute();
        assertEquals(10, levelMap.getEntities().size());
        assertEquals(2, levelMap.getEntitiesAt(3, 3).size()); // java + new paper
        assertEquals(2, levelMap.getEntitiesAt(2, 3).size()); // paper + new java
    }

    @Test
    void testProcessYouDefeatDestroysYouEntity() {
        Entity java = addEntity(TypeRegistry.JAVA, 5, 5);
        addEntity(TypeRegistry.DOCUMENT, 5, 5);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU);
        rule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.IS, TypeRegistry.DEFEAT);

        CompositeAction result = executeInteractions();
        result.execute();

        assertFalse(levelMap.getEntities().contains(java));
    }

    @Test
    void testProcessYouDefeatNoOverlap() {
        Entity java = addEntity(TypeRegistry.JAVA, 5, 5);
        addEntity(TypeRegistry.DOCUMENT, 3, 3);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU);
        rule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.IS, TypeRegistry.DEFEAT);

        CompositeAction result = executeInteractions();
        result.execute();

        assertTrue(levelMap.getEntities().contains(java));
    }

    @Test
    void testProcessHotMeltDestroysMeltEntity() {
        Entity water = addEntity(TypeRegistry.WATER, 2, 2);
        addEntity(TypeRegistry.LAVA, 2, 2);
        rule(TypeRegistry.TEXT_WATER, TypeRegistry.IS, TypeRegistry.MELT);
        rule(TypeRegistry.TEXT_LAVA, TypeRegistry.IS, TypeRegistry.HOT);

        CompositeAction result = executeInteractions();
        result.execute();

        assertFalse(levelMap.getEntities().contains(water));
    }

    @Test
    void testProcessHotMeltNoOverlap() {
        Entity water = addEntity(TypeRegistry.WATER, 2, 2);
        addEntity(TypeRegistry.LAVA, 8, 8);
        rule(TypeRegistry.TEXT_WATER, TypeRegistry.IS, TypeRegistry.MELT);
        rule(TypeRegistry.TEXT_LAVA, TypeRegistry.IS, TypeRegistry.HOT);

        CompositeAction result = executeInteractions();
        result.execute();

        assertTrue(levelMap.getEntities().contains(water));
    }

    @Test
    void testProcessSinkDestroysAllAtPosition() {
        Entity paper = addEntity(TypeRegistry.DOCUMENT, 3, 3);
        Entity python = addEntity(TypeRegistry.PYTHON, 3, 3);
        rule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.IS, TypeRegistry.SINK);

        CompositeAction result = executeInteractions();
        result.execute();

        assertFalse(levelMap.getEntities().contains(paper));
        assertFalse(levelMap.getEntities().contains(python));
        assertEquals(0, levelMap.getEntitiesAt(3, 3).size());
    }

    @Test
    void testProcessSinkNoOverlap() {
        Entity paper = addEntity(TypeRegistry.DOCUMENT, 3, 3);
        rule(TypeRegistry.TEXT_DOCUMENT, TypeRegistry.IS, TypeRegistry.SINK);

        CompositeAction result = executeInteractions();
        assertEquals(0, result.size());

        result.execute();
        assertTrue(levelMap.getEntities().contains(paper));
    }

    @Test
    void testProcessHasCreatesEntityOnDestruction() {
        Entity java = addEntity(TypeRegistry.JAVA, 5, 5);
        addEntity(TypeRegistry.PYTHON, 5, 5);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.HAS, TypeRegistry.TEXT_DOCUMENT);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU);
        rule(TypeRegistry.TEXT_PYTHON, TypeRegistry.IS, TypeRegistry.DEFEAT);

        CompositeAction result = executeInteractions();
        result.execute();

        assertFalse(levelMap.getEntities().contains(java));
        assertEquals(1, levelMap.getEntitiesAt(5, 5).stream()
                .filter(e -> e.getType() == TypeRegistry.DOCUMENT)
                .count());
    }

    @Test
    void testProcessHasNoDestruction() {
        addEntity(TypeRegistry.JAVA, 5, 5);
        rule(TypeRegistry.TEXT_JAVA, TypeRegistry.HAS, TypeRegistry.TEXT_DOCUMENT);

        CompositeAction result = executeInteractions();
        assertEquals(0, result.size());
    }
}
