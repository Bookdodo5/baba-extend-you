package model.rule;
import model.entity.Entity;
import model.entity.TypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
class RulesetTest {
    private Ruleset ruleset;
    private Entity subjectText;
    private Entity verbText;
    private Entity effectText;
    private Rule rule;

    @BeforeEach
    void setUp() {
        ruleset = new Ruleset();
        subjectText = new Entity(TypeRegistry.TEXT_JAVA);
        verbText = new Entity(TypeRegistry.IS);
        effectText = new Entity(TypeRegistry.YOU);
        rule = new Rule(subjectText, verbText, effectText, List.of());
    }

    @Test
    void testInitiallyEmpty() {
        assertTrue(ruleset.getRules().isEmpty());
    }

    @Test
    void testSetRules() {
        ruleset.setRules(List.of(rule));
        assertEquals(1, ruleset.getRules().size());
        assertTrue(ruleset.getRules().contains(rule));
    }

    @Test
    void testGetActiveTexts() {
        Entity condText = new Entity(TypeRegistry.ON);
        Entity condParam = new Entity(TypeRegistry.TEXT_DOCUMENT);
        Condition condition = new Condition(condText, condParam);
        Rule ruleWithCondition = new Rule(subjectText, verbText, effectText, List.of(condition));

        ruleset.setRules(List.of(ruleWithCondition));
        Set<Entity> texts = ruleset.getActiveTexts();

        assertTrue(texts.contains(subjectText));
        assertTrue(texts.contains(verbText));
        assertTrue(texts.contains(effectText));
        assertTrue(texts.contains(condText));
        assertTrue(texts.contains(condParam));
    }
}
