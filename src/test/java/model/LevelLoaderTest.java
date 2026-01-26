package model;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelLoader;
import model.map.LevelMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LevelLoaderTest {
    @Test
    void testLoadLevel() {
        LevelMap result = LevelLoader.loadLevel("test_level.csv");

        assertNotNull(result);
        assertEquals(5, result.getWidth());
        assertEquals(3, result.getHeight());
        assertEquals(8, result.getEntities().size());

        List<Entity> entitiesAt00 = result.getEntitiesAt(0, 0);
        assertEquals(1, entitiesAt00.size());
        assertEquals(TypeRegistry.WIRE, entitiesAt00.get(0).getType());
        assertEquals(0, result.getEntityX(entitiesAt00.get(0)));
        assertEquals(0, result.getEntityY(entitiesAt00.get(0)));
        assertEquals(Direction.DOWN, entitiesAt00.get(0).getDirection());

        List<Entity> entitiesAt32 = result.getEntitiesAt(3, 2);
        assertEquals(0, entitiesAt32.size());
    }

    @Test
    void testLoadNotFound() {
        LevelMap result = LevelLoader.loadLevel("no.csv");
        assertNull(result);
    }

    @Test
    void testLoadInvalid() {
        LevelMap result = LevelLoader.loadLevel("invalid_level.csv");
        assertNull(result);
    }

    @Test
    void testLoadEmpty() {
        LevelMap result = LevelLoader.loadLevel("empty_level.csv");
        assertNull(result);
    }

    @Test
    void testLoadStacked() {
        LevelMap result = LevelLoader.loadLevel("stacked_level.csv");
        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(4, result.getEntities().size());

        List<Entity> entitiesAt00 = result.getEntitiesAt(0, 0);
        assertEquals(2, entitiesAt00.size());
    }
}
