package model;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.AnimationStyle;
import model.map.LevelMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelMapTest {
    private LevelMap levelMap;
    private EntityType javaType;

    @BeforeEach
    void setUp() {
        levelMap = new LevelMap(10, 10);
        javaType = new EntityType(1, "java", "java.png", AnimationStyle.DIRECTIONAL);
    }

    @Test
    void testAddEntity() {
        Entity player = new Entity(javaType);
        levelMap.setEntityPosition(player, 5, 5);

        List<Entity> entitiesAt = levelMap.getEntitiesAt(5, 5);
        assertTrue(entitiesAt.contains(player));
        assertEquals(1, levelMap.getEntities().size());
    }

    @Test
    void testSetEntityPosition() {
        Entity player = new Entity(javaType);
        levelMap.setEntityPosition(player, 1, 1);

        levelMap.setEntityPosition(player, 2, 2);

        assertEquals(2, levelMap.getEntityX(player));
        assertEquals(2, levelMap.getEntityY(player));
        assertTrue(levelMap.getEntitiesAt(1, 1).isEmpty());
        assertTrue(levelMap.getEntitiesAt(2, 2).contains(player));
    }

    @Test
    void testIsInside() {
        assertTrue(levelMap.isInside(0, 0));
        assertTrue(levelMap.isInside(9, 9));
        assertFalse(levelMap.isInside(-1, 0));
        assertFalse(levelMap.isInside(10, 10));
    }

    @Test
    void testRemoveEntity() {
        Entity player = new Entity(javaType);
        levelMap.setEntityPosition(player, 0, 0);
        levelMap.removeEntity(player);

        assertTrue(levelMap.getEntitiesAt(0, 0).isEmpty());
        assertEquals(0, levelMap.getEntities().size());
    }
}