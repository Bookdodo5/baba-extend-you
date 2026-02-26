package model.rule;
import model.entity.Entity;
import model.entity.TypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class RuleTest {
    private Entity subjectText;
    private Entity verbText;
    @BeforeEach
    void setUp() {
        subjectText = new Entity(TypeRegistry.TEXT_JAVA);
        verbText = new Entity(TypeRegistry.IS);
    }
    @Test
    void testGetSubject_UnwrapsNounType() {
        Rule rule = new Rule(subjectText, verbText, new Entity(TypeRegistry.YOU), List.of());
        assertEquals(TypeRegistry.JAVA, rule.getSubject());
    }
    @Test
    void testGetVerb() {
        Rule rule = new Rule(subjectText, verbText, new Entity(TypeRegistry.YOU), List.of());
        assertEquals(TypeRegistry.IS, rule.getVerb());
    }
    @Test
    void testGetEffect_PropertyType() {
        Rule rule = new Rule(subjectText, verbText, new Entity(TypeRegistry.YOU), List.of());
        assertEquals(TypeRegistry.YOU, rule.getEffect());
    }
    @Test
    void testGetEffect_NounType_UnwrapsToReferencedType() {
        // JAVA IS DOCUMENT ? TEXT_DOCUMENT (NounType) should resolve to DOCUMENT
        Rule rule = new Rule(subjectText, verbText, new Entity(TypeRegistry.TEXT_DOCUMENT), List.of());
        assertEquals(TypeRegistry.DOCUMENT, rule.getEffect());
    }
    @Test
    void testGetConditions_Empty() {
        Rule rule = new Rule(subjectText, verbText, new Entity(TypeRegistry.YOU), List.of());
        assertTrue(rule.getConditions().isEmpty());
    }
    @Test
    void testGetConditions_WithCondition() {
        Condition condition = new Condition(new Entity(TypeRegistry.ON), new Entity(TypeRegistry.TEXT_DOCUMENT));
        Rule rule = new Rule(subjectText, verbText, new Entity(TypeRegistry.YOU), List.of(condition));
        assertEquals(1, rule.getConditions().size());
        assertEquals(condition, rule.getConditions().get(0));
    }
    @Test
    void testToString() {
        Rule rule = new Rule(subjectText, verbText, new Entity(TypeRegistry.YOU), List.of());
        assertEquals("java is you", rule.toString());
    }
    @Test
    void testToString_WithCondition() {
        Condition condition = new Condition(new Entity(TypeRegistry.ON), new Entity(TypeRegistry.TEXT_DOCUMENT));
        Rule rule = new Rule(subjectText, verbText, new Entity(TypeRegistry.YOU), List.of(condition));
        assertEquals("java (on document) is you", rule.toString());
    }
}
