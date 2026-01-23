package logic.rule.evaluator;


import model.entity.Direction;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ConditionEvaluatorTest {
    private ConditionEvaluator evaluator;
    private LevelMap levelMap;
    private Entity java;
    private Entity rock;

    @BeforeEach
    void setUp() {
        evaluator = new ConditionEvaluator();
        levelMap = new LevelMap(10, 10);
        java = new Entity(TypeRegistry.JAVA, 5, 5);
        rock = new Entity(TypeRegistry.PAPER, 6, 5);
        java.setDirection(Direction.RIGHT);
        rock.setDirection(Direction.RIGHT);
        levelMap.addEntity(java);
        levelMap.addEntity(rock);
    }

    @Test
    void testEvaluateEmptyConditions() {
        assertTrue(evaluator.evaluate(java, new ArrayList<>(), levelMap));
    }

    @Test
    void testOnCondition() {
        Entity onText = new Entity(TypeRegistry.ON, 0, 0);
        Entity rockText = new Entity(TypeRegistry.TEXT_PAPER, 1, 1);
        Condition onRock = new Condition(onText, rockText);
        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(onRock);

        //rock at (6,5) + java at (5,5)
        assertFalse(evaluator.evaluate(java, conditions, levelMap));

        //rock at (5,5) + java at (5,5)
        levelMap.setEntityPosition(rock, 5, 5);
        assertTrue(evaluator.evaluate(java, conditions, levelMap));
    }

    @Test
    void testNearCondition() {
        Entity nearText = new Entity(TypeRegistry.NEAR, 0, 0);
        Entity rockText = new Entity(TypeRegistry.TEXT_PAPER, 1, 1);
        Condition nearRock = new Condition(nearText, rockText);
        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(nearRock);

        //rock at (6,5) + java at (5,5)
        assertTrue(evaluator.evaluate(java, conditions, levelMap));

        //rock at (8,8) + baba at (5,5)
        levelMap.setEntityPosition(rock, 8, 8);
        assertFalse(evaluator.evaluate(java, conditions, levelMap));

        //rock at (7,5) + baba at (5,5)
        levelMap.setEntityPosition(rock, 7, 5);
        assertFalse(evaluator.evaluate(java, conditions, levelMap));

        //rock at (5,5) + baba at (5,5)
        levelMap.setEntityPosition(rock, 5, 5);
        assertTrue(evaluator.evaluate(java, conditions, levelMap));

        //rock at (4,4) + baba at (5,5)
        levelMap.setEntityPosition(rock, 4, 4);
        assertTrue(evaluator.evaluate(java, conditions, levelMap));
    }

    @Test
    void testFacingCondition() {
        Entity facingText = new Entity(TypeRegistry.FACING, 0, 0);
        Entity rockText = new Entity(TypeRegistry.TEXT_PAPER, 1, 1);
        Condition facingRock = new Condition(facingText, rockText);
        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(facingRock);

        // java is at (5,5) facing RIGHT, rock is at (6,5)
        assertTrue(evaluator.evaluate(java, conditions, levelMap));

        // java is at (5,5) facing LEFT, rock is at (6,5)
        java.setDirection(Direction.LEFT);
        assertFalse(evaluator.evaluate(java, conditions, levelMap));

        // java is at (5,5) facing UP, rock is at (6,5)
        java.setDirection(Direction.UP);
        assertFalse(evaluator.evaluate(java, conditions, levelMap));

        // java is at (5,5) facing UP, rock is at (5,4)
        levelMap.setEntityPosition(rock, 5, 4);
        assertTrue(evaluator.evaluate(java, conditions, levelMap));

        // java is at (5,5) facing UP, rock is at (5,3)5
        levelMap.setEntityPosition(rock, 5, 3);
        assertFalse(evaluator.evaluate(java, conditions, levelMap));
    }
}