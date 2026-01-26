package logic.rule.evaluator;


import model.entity.Direction;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Rule;
import model.rule.Ruleset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionEvaluatorTest {
    private ConditionEvaluator evaluator;
    private LevelMap levelMap;
    private Entity java;
    private Entity paper;
    private Ruleset ruleset;

    @BeforeEach
    void setUp() {
        evaluator = new ConditionEvaluator();
        levelMap = new LevelMap(10, 10);
        ruleset = new Ruleset();
        java = new Entity(TypeRegistry.JAVA);
        paper = new Entity(TypeRegistry.PAPER);
        java.setDirection(Direction.RIGHT);
        paper.setDirection(Direction.RIGHT);
        levelMap.setEntityPosition(java, 5, 5);
        levelMap.setEntityPosition(paper, 6, 5);
    }

    @Test
    void testEvaluateEmptyConditions() {
        assertTrue(evaluator.evaluate(java, new ArrayList<>(), levelMap, ruleset));
    }

    @Test
    void testOnCondition() {
        Entity onText = new Entity(TypeRegistry.ON);
        Entity paperText = new Entity(TypeRegistry.TEXT_PAPER);
        Condition onPaper = new Condition(onText, paperText);
        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(onPaper);

        //paper at (6,5) + java at (5,5)
        assertFalse(evaluator.evaluate(java, conditions, levelMap, ruleset));

        //paper at (5,5) + java at (5,5)
        levelMap.setEntityPosition(paper, 5, 5);
        assertTrue(evaluator.evaluate(java, conditions, levelMap, ruleset));
    }

    @Test
    void testNearCondition() {
        Entity nearText = new Entity(TypeRegistry.NEAR);
        Entity paperText = new Entity(TypeRegistry.TEXT_PAPER);
        Condition nearPaper = new Condition(nearText, paperText);
        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(nearPaper);

        //paper at (6,5) + java at (5,5)
        assertTrue(evaluator.evaluate(java, conditions, levelMap, ruleset));

        //paper at (8,8) + baba at (5,5)
        levelMap.setEntityPosition(paper, 8, 8);
        assertFalse(evaluator.evaluate(java, conditions, levelMap, ruleset));

        //paper at (7,5) + baba at (5,5)
        levelMap.setEntityPosition(paper, 7, 5);
        assertFalse(evaluator.evaluate(java, conditions, levelMap, ruleset));

        //paper at (5,5) + baba at (5,5)
        levelMap.setEntityPosition(paper, 5, 5);
        assertTrue(evaluator.evaluate(java, conditions, levelMap, ruleset));

        //paper at (4,4) + baba at (5,5)
        levelMap.setEntityPosition(paper, 4, 4);
        assertTrue(evaluator.evaluate(java, conditions, levelMap, ruleset));
    }

    @Test
    void testFacingCondition() {
        Entity facingText = new Entity(TypeRegistry.FACING);
        Entity paperText = new Entity(TypeRegistry.TEXT_PAPER);
        Condition facingPaper = new Condition(facingText, paperText);
        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(facingPaper);

        // java is at (5,5) facing RIGHT, paper is at (6,5)
        assertTrue(evaluator.evaluate(java, conditions, levelMap, ruleset));

        // java is at (5,5) facing LEFT, paper is at (6,5)
        java.setDirection(Direction.LEFT);
        assertFalse(evaluator.evaluate(java, conditions, levelMap, ruleset));

        // java is at (5,5) facing UP, paper is at (6,5)
        java.setDirection(Direction.UP);
        assertFalse(evaluator.evaluate(java, conditions, levelMap, ruleset));

        // java is at (5,5) facing UP, paper is at (5,4)
        levelMap.setEntityPosition(paper, 5, 4);
        assertTrue(evaluator.evaluate(java, conditions, levelMap, ruleset));

        // java is at (5,5) facing UP, paper is at (5,3)5
        levelMap.setEntityPosition(paper, 5, 3);
        assertFalse(evaluator.evaluate(java, conditions, levelMap, ruleset));
    }
    
    @Test
    void testInstanceofCondition() {
        Entity error = new Entity(TypeRegistry.ERROR);
        levelMap.setEntityPosition(error, 7, 7);

        // ERROR instanceof ERROR should be true
        Entity instanceofText = new Entity(TypeRegistry.INSTANCEOF);
        Entity errorText = new Entity(TypeRegistry.TEXT_ERROR);
        Condition instanceofError = new Condition(instanceofText, errorText);
        ArrayList<Condition> conditions1 = new ArrayList<>();
        conditions1.add(instanceofError);

        assertTrue(evaluator.evaluate(error, conditions1, levelMap, ruleset));

        // ERROR instanceof PAPER should be false
        Entity paperText = new Entity(TypeRegistry.TEXT_PAPER);
        Condition instanceofPaper = new Condition(instanceofText, paperText);
        ArrayList<Condition> conditions2 = new ArrayList<>();
        conditions2.add(instanceofPaper);

        assertFalse(evaluator.evaluate(error, conditions2, levelMap, ruleset));

        // ERROR EXTEND WARNING, then ERROR instanceof WARNING should be true
        Entity extendText = new Entity(TypeRegistry.EXTEND);
        Entity warningText = new Entity(TypeRegistry.TEXT_WARNING);
        Entity warning = new Entity(TypeRegistry.WARNING);
        Rule errorExtendsWarning = new Rule(errorText, extendText, warningText, List.of());
        ruleset.setRules(List.of(errorExtendsWarning));

        Condition instanceofWarning = new Condition(instanceofText, warningText);
        ArrayList<Condition> conditions3 = new ArrayList<>();
        conditions3.add(instanceofWarning);

        assertTrue(evaluator.evaluate(error, conditions3, levelMap, ruleset));

        // WARNING instanceof ERROR should be false
        assertFalse(evaluator.evaluate(warning, conditions1, levelMap, ruleset));

        // ERROR instanceof PAPER should still be false
        assertFalse(evaluator.evaluate(error, conditions2, levelMap, ruleset));

        // ERROR EXTEND WARNING, WARNING EXTEND JAVA, then ERROR instanceof JAVA should be true
        Entity javaText = new Entity(TypeRegistry.TEXT_JAVA);
        Rule warningExtendsJava = new Rule(warningText, extendText, javaText, List.of());
        ruleset.setRules(List.of(errorExtendsWarning, warningExtendsJava));

        Condition instanceofJava = new Condition(instanceofText, javaText);
        ArrayList<Condition> conditions4 = new ArrayList<>();
        conditions4.add(instanceofJava);

        assertTrue(evaluator.evaluate(error, conditions4, levelMap, ruleset));

        // JAVA instanceof ERROR should be false
        assertFalse(evaluator.evaluate(java, conditions1, levelMap, ruleset));

        // ERROR instanceof ERROR should still be true
        assertTrue(evaluator.evaluate(error, conditions1, levelMap, ruleset));
    }
}