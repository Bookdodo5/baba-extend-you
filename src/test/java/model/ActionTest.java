package model;

import model.action.ActionStack;
import model.action.CompositeAction;
import model.action.CreateAction;
import model.action.DestroyAction;
import model.action.MoveAction;
import model.action.RotateAction;
import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.AnimationStyle;
import model.map.LevelMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionTest {
    private LevelMap levelMap;
    private ActionStack actionStack;
    private Entity entity;

    @BeforeEach
    void setUp() {
        levelMap = new LevelMap(10, 10);
        actionStack = new ActionStack();
        EntityType javaType = new EntityType(1, "java", "java.png", AnimationStyle.CHARACTER);
        entity = new Entity(javaType, 0, 0, Direction.RIGHT);
        levelMap.addEntity(entity);
    }

    @Test
    void testMoveAction() {
        MoveAction move = new MoveAction(levelMap, entity, 1, 1);

        move.execute();
        assertEquals(1, entity.getPosX());
        assertEquals(1, entity.getPosY());

        move.undo();
        assertEquals(0, entity.getPosX());
        assertEquals(0, entity.getPosY());
    }

    @Test
    void testRotateAction() {
        RotateAction rotate = new RotateAction(entity, Direction.LEFT);

        assertEquals(Direction.RIGHT, entity.getDirection());

        rotate.execute();
        assertEquals(Direction.LEFT, entity.getDirection());

        rotate.undo();
        assertEquals(Direction.RIGHT, entity.getDirection());
    }

    @Test
    void testDestroyAction() {
        DestroyAction destroy = new DestroyAction(levelMap, entity);

        assertTrue(levelMap.getEntities().contains(entity));
        assertTrue(levelMap.getEntitiesAt(0, 0).contains(entity));

        destroy.execute();
        assertFalse(levelMap.getEntities().contains(entity));
        assertFalse(levelMap.getEntitiesAt(0, 0).contains(entity));

        destroy.undo();
        assertTrue(levelMap.getEntities().contains(entity));
        assertTrue(levelMap.getEntitiesAt(0, 0).contains(entity));
    }

    @Test
    void testActionStackEmpty() {
        assertDoesNotThrow(() -> actionStack.undo());
        assertDoesNotThrow(() -> actionStack.redo());
    }

    @Test
    void testActionStackClearRedo() {
        CompositeAction action1 = new CompositeAction();
        action1.add(new RotateAction(entity, Direction.UP));
        
        CompositeAction action2 = new CompositeAction();
        action2.add(new RotateAction(entity, Direction.DOWN));

        action1.execute();
        actionStack.newAction(action1);
        actionStack.undo(); //action1 in redo stack
        
        actionStack.newAction(action2); //redo stack cleared
        
        actionStack.redo(); //nothing
        assertEquals(Direction.RIGHT, entity.getDirection());
    }

    @Test
    void testActionStackUndoRedo() {
        CompositeAction composite = new CompositeAction();
        EntityType pythonType = new EntityType(2, "python", "python.png", AnimationStyle.CHARACTER);
        composite.add(new MoveAction(levelMap, entity, 5, 5));
        composite.add(new CreateAction(levelMap, pythonType, 6, 6));

        composite.execute();
        actionStack.newAction(composite);

        assertEquals(5, entity.getPosX());
        assertEquals(1, levelMap.getEntitiesAt(6, 6).size());
        assertEquals(2, levelMap.getEntities().size());

        actionStack.undo();
        assertEquals(0, entity.getPosX());
        assertEquals(0, levelMap.getEntitiesAt(6, 6).size());
        assertEquals(1, levelMap.getEntities().size());

        actionStack.redo();
        assertEquals(5, entity.getPosX());
        assertEquals(1, levelMap.getEntitiesAt(6, 6).size());
        assertEquals(2, levelMap.getEntities().size());
    }
}