package logic.rule.parser;

import model.entity.Entity;
import model.entity.TypeRegistry;
import model.rule.Rule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SemanticValidatorTest {

    @Test
    void testVerbValidation() {
        SemanticValidator validator = new SemanticValidator();
        Entity rock = new Entity(TypeRegistry.TEXT_PAPER, 0, 0);
        Entity is = new Entity(TypeRegistry.IS, 1, 0);
        Entity has = new Entity(TypeRegistry.HAS, 2, 0);
        Entity wire = new Entity(TypeRegistry.TEXT_WIRE, 3, 0);
        Entity sink = new Entity(TypeRegistry.SINK, 3, 0);

        Rule nounIsNoun = new Rule(rock, is, wire, new ArrayList<>());
        Rule nounHasNoun = new Rule(rock, has, wire, new ArrayList<>());
        Rule nounIsProperty = new Rule(rock, is, sink, new ArrayList<>());
        Rule nounHasProperty = new Rule(rock, has, sink, new ArrayList<>());

        List<Rule> rules = List.of(nounIsNoun, nounIsProperty, nounHasNoun, nounHasProperty);

        List<Rule> validRules = validator.validate(rules);
        assertEquals(3, validRules.size());
        assertFalse(validRules.contains(nounHasProperty));
    }
}
