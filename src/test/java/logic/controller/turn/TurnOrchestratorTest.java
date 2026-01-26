package logic.controller.turn;

import logic.rule.parser.RuleParser;
import model.action.CompositeAction;
import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Ruleset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurnOrchestratorTest {
    private TurnOrchestrator turnOrchestrator;
    private RuleParser ruleParser;
    private LevelMap levelMap;
    private Ruleset ruleset;

    @BeforeEach
    void setUp() {
        turnOrchestrator = new TurnOrchestrator();
        ruleParser = new RuleParser();
        levelMap = new LevelMap(10, 10);
        ruleset = new Ruleset();
    }

    private Entity setEntityPosition(EntityType type, int x, int y, Direction direction) {
        Entity entity = new Entity(type);
        entity.setDirection(direction);
        levelMap.setEntityPosition(entity, x, y);
        return entity;
    }

    private Entity setEntityPosition(EntityType type, int x, int y) {
        return setEntityPosition(type, x, y, Direction.RIGHT);
    }

    private void addRule(EntityType subject, EntityType verb, EntityType effect, int row) {
        setEntityPosition(subject, 0, row);
        setEntityPosition(verb, 1, row);
        setEntityPosition(effect, 2, row);
    }

    private CompositeAction runTurn(Direction direction) {
        ruleset.setRules(ruleParser.parseRules(levelMap));
        return turnOrchestrator.runTurn(direction, levelMap, ruleset, ruleParser);
    }

    @Test
    void testRunTurnNoEntities() {
        CompositeAction result = runTurn(Direction.RIGHT);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testRunTurnSimpleYou() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 5, 5);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU, 0);

        CompositeAction result1 = runTurn(Direction.DOWN);
        assertNotNull(result1);
        assertEquals(1, result1.size());
        assertEquals(5, levelMap.getEntityX(javaEntity));
        assertEquals(6, levelMap.getEntityY(javaEntity));

        CompositeAction result2 = runTurn(Direction.LEFT);
        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertEquals(4, levelMap.getEntityX(javaEntity));
        assertEquals(6, levelMap.getEntityY(javaEntity));

        CompositeAction result3 = runTurn(Direction.UP);
        assertNotNull(result3);
        assertEquals(1, result3.size());
        assertEquals(4, levelMap.getEntityX(javaEntity));
        assertEquals(5, levelMap.getEntityY(javaEntity));

        CompositeAction result4 = runTurn(Direction.RIGHT);
        assertNotNull(result4);
        assertEquals(1, result4.size());
        assertEquals(5, levelMap.getEntityX(javaEntity));
        assertEquals(5, levelMap.getEntityY(javaEntity));
    }

    @Test
    void testRunTurnYouWithPush() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 5, 5);
        Entity paperEntity = setEntityPosition(TypeRegistry.PAPER, 6, 5);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU, 0);
        addRule(TypeRegistry.TEXT_PAPER, TypeRegistry.IS, TypeRegistry.PUSH, 1);

        runTurn(Direction.RIGHT);
        assertEquals(6, levelMap.getEntityX(javaEntity));
        assertEquals(5, levelMap.getEntityY(javaEntity));
        assertEquals(7, levelMap.getEntityX(paperEntity));
        assertEquals(5, levelMap.getEntityY(paperEntity));

        runTurn(Direction.DOWN);
        runTurn(Direction.RIGHT);
        runTurn(Direction.UP);
        assertEquals(7, levelMap.getEntityX(javaEntity));
        assertEquals(5, levelMap.getEntityY(javaEntity));
        assertEquals(7, levelMap.getEntityX(paperEntity));
        assertEquals(4, levelMap.getEntityY(paperEntity));
    }

    @Test
    void testRunTurnYouWithStop() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 9, 5);
        Entity paperEntity = setEntityPosition(TypeRegistry.PAPER, 8, 5);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU, 0);
        addRule(TypeRegistry.TEXT_PAPER, TypeRegistry.IS, TypeRegistry.STOP, 1);

        runTurn(Direction.RIGHT); // blocked by boundary
        assertEquals(9, levelMap.getEntityX(javaEntity));
        assertEquals(5, levelMap.getEntityY(javaEntity));

        runTurn(Direction.LEFT); // blocked by paper
        assertEquals(9, levelMap.getEntityX(javaEntity));
        assertEquals(5, levelMap.getEntityY(javaEntity));
    }

    @Test
    void testRunTurnWithMoveProperty() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 5, 5, Direction.DOWN);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.MOVE, 0);

        runTurn(Direction.LEFT);
        assertEquals(5, levelMap.getEntityX(javaEntity));
        assertEquals(6, levelMap.getEntityY(javaEntity));

        runTurn(Direction.RIGHT);
        assertEquals(5, levelMap.getEntityX(javaEntity));
        assertEquals(7, levelMap.getEntityY(javaEntity));
    }

    @Test
    void testRunTurnWithTransformation() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 5, 5);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.TEXT_PAPER, 0);

        runTurn(Direction.DOWN);
        assertFalse(levelMap.getEntities().contains(javaEntity));
        assertEquals(1, levelMap.getEntitiesAt(5, 5).size());
        assertEquals(TypeRegistry.PAPER, levelMap.getEntitiesAt(5, 5).getFirst().getType());
    }

    @Test
    void testRunTurnWithYouDefeat() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 4, 5);
        Entity paperEntity = setEntityPosition(TypeRegistry.PAPER, 5, 5);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU, 0);
        addRule(TypeRegistry.TEXT_PAPER, TypeRegistry.IS, TypeRegistry.DEFEAT, 1);

        runTurn(Direction.RIGHT);
        assertFalse(levelMap.getEntities().contains(javaEntity));
        assertTrue(levelMap.getEntities().contains(paperEntity));
    }

    @Test
    void testRunTurnWithSink() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 4, 5);
        Entity paperEntity = setEntityPosition(TypeRegistry.PAPER, 5, 5);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.SINK, 0);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU, 1);

        runTurn(Direction.RIGHT);
        assertFalse(levelMap.getEntities().contains(javaEntity));
        assertFalse(levelMap.getEntities().contains(paperEntity));
        assertEquals(0, levelMap.getEntitiesAt(5, 5).size());
    }

    @Test
    void testRunTurnWithHotMelt() {
        Entity lavaEntity = setEntityPosition(TypeRegistry.LAVA, 4, 5);
        Entity waterEntity = setEntityPosition(TypeRegistry.WATER, 5, 5);
        addRule(TypeRegistry.TEXT_LAVA, TypeRegistry.IS, TypeRegistry.YOU, 0);
        addRule(TypeRegistry.TEXT_LAVA, TypeRegistry.IS, TypeRegistry.HOT, 1);
        addRule(TypeRegistry.TEXT_WATER, TypeRegistry.IS, TypeRegistry.MELT, 2);

        runTurn(Direction.RIGHT);
        assertTrue(levelMap.getEntities().contains(lavaEntity));
        assertFalse(levelMap.getEntities().contains(waterEntity));
    }

    @Test
    void testRunTurnComplexScenario() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 5, 5, Direction.RIGHT);
        Entity paperEntity = setEntityPosition(TypeRegistry.PAPER, 7, 5, Direction.LEFT);
        Entity pythonEntity = setEntityPosition(TypeRegistry.PYTHON, 8, 8);

        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU, 0);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.STOP, 1);
        addRule(TypeRegistry.TEXT_PAPER, TypeRegistry.IS, TypeRegistry.MOVE, 2);
        addRule(TypeRegistry.TEXT_PYTHON, TypeRegistry.IS, TypeRegistry.TEXT_FLAG, 3);
        addRule(TypeRegistry.TEXT_FLAG, TypeRegistry.IS, TypeRegistry.SINK, 4);
        addRule(TypeRegistry.TEXT_FLAG, TypeRegistry.HAS, TypeRegistry.TEXT_PAPER, 5);

        runTurn(Direction.RIGHT);
        assertEquals(6, levelMap.getEntityX(javaEntity));
        assertEquals(8, levelMap.getEntityX(paperEntity));
        assertEquals(Direction.RIGHT, paperEntity.getDirection());
        assertFalse(levelMap.getEntities().contains(pythonEntity));
        assertEquals(TypeRegistry.FLAG, levelMap.getEntitiesAt(8, 8).getFirst().getType());

        runTurn(Direction.RIGHT);
        runTurn(Direction.RIGHT);
        runTurn(Direction.DOWN);
        runTurn(Direction.DOWN);
        runTurn(Direction.DOWN);
        assertFalse(levelMap.getEntities().contains(javaEntity));
        assertEquals(1, levelMap.getEntitiesAt(8, 8).size());
        assertEquals(TypeRegistry.PAPER, levelMap.getEntitiesAt(8, 8).getFirst().getType());
    }

    @Test
    void testRunTurnRulesReparsedAfterMove() {
        Entity javaEntity = setEntityPosition(TypeRegistry.JAVA, 5, 5);
        setEntityPosition(TypeRegistry.TEXT_PAPER, 6, 4);
        setEntityPosition(TypeRegistry.IS, 6, 5);
        setEntityPosition(TypeRegistry.PUSH, 6, 6);
        Entity paperEntity = setEntityPosition(TypeRegistry.PAPER, 5, 6);

        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU, 0);

        runTurn(Direction.DOWN);
        assertTrue(levelMap.getEntitiesAt(5, 6).contains(javaEntity));
        assertTrue(levelMap.getEntitiesAt(5, 7).contains(paperEntity));

        runTurn(Direction.RIGHT); // PAPER IS PUSH now broken
        runTurn(Direction.LEFT);
        runTurn(Direction.DOWN);
        assertTrue(levelMap.getEntitiesAt(5, 7).contains(javaEntity));
        assertTrue(levelMap.getEntitiesAt(5, 7).contains(paperEntity));
    }

    @Test
    void testRunTurnMultipleYouEntities() {
        Entity javaEntity1 = setEntityPosition(TypeRegistry.JAVA, 5, 8);
        Entity javaEntity2 = setEntityPosition(TypeRegistry.JAVA, 5, 9);
        Entity paperEntity1 = setEntityPosition(TypeRegistry.PAPER, 7, 8);
        Entity paperEntity2 = setEntityPosition(TypeRegistry.PAPER, 7, 9);
        Entity pythonEntity1 = setEntityPosition(TypeRegistry.PYTHON, 9, 6, Direction.DOWN);
        Entity pythonEntity2 = setEntityPosition(TypeRegistry.PYTHON, 9, 8, Direction.DOWN);
        addRule(TypeRegistry.TEXT_JAVA, TypeRegistry.IS, TypeRegistry.YOU, 0);
        addRule(TypeRegistry.TEXT_PAPER, TypeRegistry.IS, TypeRegistry.YOU, 1);
        addRule(TypeRegistry.TEXT_PYTHON, TypeRegistry.IS, TypeRegistry.YOU, 2);
        addRule(TypeRegistry.TEXT_PAPER, TypeRegistry.IS, TypeRegistry.STOP, 3);
        addRule(TypeRegistry.TEXT_PYTHON, TypeRegistry.IS, TypeRegistry.MOVE, 4);

        runTurn(Direction.DOWN);
        assertTrue(levelMap.getEntitiesAt(5, 9).contains(javaEntity1));
        assertTrue(levelMap.getEntitiesAt(5, 9).contains(javaEntity2));
        assertTrue(levelMap.getEntitiesAt(7, 8).contains(paperEntity1));
        assertTrue(levelMap.getEntitiesAt(7, 9).contains(paperEntity2));
        assertTrue(levelMap.getEntitiesAt(9, 8).contains(pythonEntity1));
        assertTrue(levelMap.getEntitiesAt(9, 8).contains(pythonEntity2));
    }
}
