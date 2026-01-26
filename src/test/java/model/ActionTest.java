package model;

import model.action.*;
import model.entity.*;
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
        EntityType javaType = new EntityType(1, "java", "java.png", AnimationStyle.DIRECTIONAL);
        entity = new Entity(javaType);
        entity.setDirection(Direction.RIGHT);
        levelMap.setEntityPosition(entity, 0, 0);
    }

    @Test
    void testMoveAction() {
        MoveAction move = new MoveAction(levelMap, entity, Direction.DOWN);

        move.execute();
        assertEquals(0, levelMap.getEntityX(entity));
        assertEquals(1, levelMap.getEntityY(entity));
        assertEquals(Direction.DOWN, entity.getDirection());

        move.undo();
        assertEquals(0, levelMap.getEntityX(entity));
        assertEquals(0, levelMap.getEntityY(entity));
        assertEquals(Direction.RIGHT, entity.getDirection());
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
        composite.add(new MoveAction(levelMap, entity, Direction.DOWN));
        composite.add(new CreateAction(levelMap, TypeRegistry.PYTHON, 6, 6));

        composite.execute();
        actionStack.newAction(composite);

        assertEquals(1, levelMap.getEntityY(entity));
        assertEquals(1, levelMap.getEntitiesAt(6, 6).size());
        assertEquals(1, levelMap.getEntitiesAt(0, 1).size());
        assertEquals(Direction.DOWN, levelMap.getEntitiesAt(0, 1).get(0).getDirection());
        assertEquals(2, levelMap.getEntities().size());

        actionStack.undo();
        assertEquals(0, levelMap.getEntityY(entity));
        assertEquals(0, levelMap.getEntitiesAt(6, 6).size());
        assertEquals(0, levelMap.getEntitiesAt(0, 1).size());
        assertEquals(1, levelMap.getEntitiesAt(0, 0).size());
        assertEquals(Direction.RIGHT, levelMap.getEntitiesAt(0, 0).get(0).getDirection());
        assertEquals(1, levelMap.getEntities().size());

        actionStack.redo();
        assertEquals(1, levelMap.getEntityY(entity));
        assertEquals(1, levelMap.getEntitiesAt(6, 6).size());
        assertEquals(1, levelMap.getEntitiesAt(0, 1).size());
        assertEquals(Direction.DOWN, levelMap.getEntitiesAt(0, 1).get(0).getDirection());
        assertEquals(2, levelMap.getEntities().size());
    }
}