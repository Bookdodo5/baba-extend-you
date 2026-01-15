package model;

import model.entity.Direction;
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
        javaType = new EntityType(1, "java", "java.png", AnimationStyle.CHARACTER);
    }

    @Test
    void testAddEntity() {
        Entity player = new Entity(javaType, 5, 5);
        levelMap.addEntity(player);

        List<Entity> entitiesAt = levelMap.getEntitiesAt(5, 5);
        assertTrue(entitiesAt.contains(player));
        assertEquals(1, levelMap.getEntities().size());
    }

    @Test
    void testMoveEntity() {
        Entity player = new Entity(javaType, 1, 1);
        levelMap.addEntity(player);

        levelMap.moveEntity(player, 2, 2);

        assertEquals(2, player.getPosX());
        assertEquals(2, player.getPosY());
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
        Entity player = new Entity(javaType, 0, 0);
        levelMap.addEntity(player);
        levelMap.removeEntity(player);

        assertTrue(levelMap.getEntitiesAt(0, 0).isEmpty());
        assertEquals(0, levelMap.getEntities().size());
    }
}