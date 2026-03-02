package model.rule;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.entity.word.ConditionType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ConditionTest {

    @Test
    void testGetCondition() {
        Condition c = new Condition(
                new Entity(TypeRegistry.ON),
                new Entity(TypeRegistry.TEXT_DOCUMENT)
        );
        assertEquals(TypeRegistry.ON, c.getCondition());
    }

    @Test
    void testGetParameter() {
        Condition c = new Condition(
                new Entity(TypeRegistry.ON),
                new Entity(TypeRegistry.TEXT_DOCUMENT)
        );
        assertEquals(TypeRegistry.DOCUMENT, c.getParameter());
    }

    @Test
    void testGetTexts() {
        Entity onText = new Entity(TypeRegistry.ON);
        Entity docText = new Entity(TypeRegistry.TEXT_DOCUMENT);
        Condition c = new Condition(onText, docText);
        assertEquals(onText, c.getConditionText());
        assertEquals(docText, c.getParameterText());
    }
}
