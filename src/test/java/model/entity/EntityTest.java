package model.entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {
    private Entity entity;

    @BeforeEach
    void setUp() {
        entity = new Entity(TypeRegistry.JAVA);
    }

    @Test
    void testConstructor() {
        assertEquals(TypeRegistry.JAVA, entity.getType());
        assertEquals(Direction.DOWN, entity.getDirection());
    }

    @Test
    void testSetDirection() {
        entity.setDirection(Direction.UP);
        assertEquals(Direction.UP, entity.getDirection());
    }

    @Test
    void testEqualsCopy() {
        assertEquals(entity, new Entity(entity));
    }

    @Test
    void testEqualsNew() {
        assertNotEquals(new Entity(TypeRegistry.JAVA), entity);
    }
}
