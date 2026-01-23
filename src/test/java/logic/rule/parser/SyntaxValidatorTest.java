package logic.rule.parser;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.rule.Condition;
import model.rule.Rule;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SyntaxValidatorTest {

    @Test
    void testSubjectVerbEffect() {
        SyntaxValidator validator = new SyntaxValidator();
        List<Entity> sentence = List.of(
                new Entity(TypeRegistry.TEXT_JAVA, 0, 0),
                new Entity(TypeRegistry.IS, 1, 0),
                new Entity(TypeRegistry.YOU, 2, 0)
        );

        List<Rule> rules = validator.validate(List.of(sentence));
        assertEquals(1, rules.size());

        Rule r = rules.get(0);
        assertEquals(TypeRegistry.JAVA, r.getSubject());
        assertEquals(TypeRegistry.IS, r.getVerb());
        assertEquals(TypeRegistry.YOU, r.getEffect());
        assertEquals(0, r.getConditions().size());
    }

    @Test
    void testMultipleSubjectAndEffect() {
        SyntaxValidator validator = new SyntaxValidator();
        List<Entity> sentence = List.of(
                new Entity(TypeRegistry.TEXT_JAVA, 0, 0),
                new Entity(TypeRegistry.AND, 1, 0),
                new Entity(TypeRegistry.TEXT_PYTHON, 2, 0),
                new Entity(TypeRegistry.IS, 3, 0),
                new Entity(TypeRegistry.YOU, 4, 0),
                new Entity(TypeRegistry.AND, 5, 0),
                new Entity(TypeRegistry.PUSH, 6, 0)
        );

        List<Rule> rules = validator.validate(List.of(sentence));
        assertEquals(4, rules.size());

        // collect seen subject and effect types
        Set<EntityType> subjects = new HashSet<>();
        Set<EntityType> effects = new HashSet<>();
        for (Rule r : rules) {
            subjects.add(r.getSubject());
            effects.add(r.getEffect());
        }

        assertTrue(subjects.contains(TypeRegistry.JAVA));
        assertTrue(subjects.contains(TypeRegistry.PYTHON));
        assertTrue(effects.contains(TypeRegistry.YOU));
        assertTrue(effects.contains(TypeRegistry.PUSH));
    }

    @Test
    void testSentenceWithCondition() {
        SyntaxValidator validator = new SyntaxValidator();
        List<Entity> sentence = List.of(
                new Entity(TypeRegistry.TEXT_JAVA, 0, 0),
                new Entity(TypeRegistry.ON, 1, 0),
                new Entity(TypeRegistry.TEXT_PAPER, 2, 0),
                new Entity(TypeRegistry.IS, 3, 0),
                new Entity(TypeRegistry.YOU, 4, 0)
        );

        List<Rule> rules = validator.validate(List.of(sentence));
        assertEquals(1, rules.size());

        Rule r = rules.get(0);
        assertEquals(TypeRegistry.JAVA, r.getSubject());
        assertEquals(TypeRegistry.IS, r.getVerb());
        assertEquals(TypeRegistry.YOU, r.getEffect());
        assertEquals(1, r.getConditions().size());

        Condition c = r.getConditions().get(0);
        assertEquals(TypeRegistry.ON, c.getCondition());
        assertEquals(TypeRegistry.PAPER, c.getParameter());
    }

    @Test
    void testMultipleCondition() {
        SyntaxValidator validator = new SyntaxValidator();
        List<Entity> sentence = List.of(
                new Entity(TypeRegistry.TEXT_FILE, 0, 0),
                new Entity(TypeRegistry.ON, 1, 0),
                new Entity(TypeRegistry.TEXT_XORGATE, 2, 0),
                new Entity(TypeRegistry.AND, 3, 0),
                new Entity(TypeRegistry.NEAR, 4, 0),
                new Entity(TypeRegistry.TEXT_WARNING, 5, 0),
                new Entity(TypeRegistry.AND, 6, 0),
                new Entity(TypeRegistry.TEXT_CHECK, 7, 0),
                new Entity(TypeRegistry.IS, 8, 0),
                new Entity(TypeRegistry.TEXT_ERROR, 9, 0)
        );

        List<Rule> rules = validator.validate(List.of(sentence));
        assertEquals(1, rules.size());

        Rule r = rules.get(0);
        assertEquals(TypeRegistry.FILE, r.getSubject());
        assertEquals(TypeRegistry.IS, r.getVerb());
        assertEquals(TypeRegistry.TEXT_ERROR, r.getEffect());
        assertEquals(3, r.getConditions().size());

        Condition c1 = r.getConditions().get(0);
        assertEquals(TypeRegistry.ON, c1.getCondition());
        assertEquals(TypeRegistry.XORGATE, c1.getParameter());

        Condition c2 = r.getConditions().get(1);
        assertEquals(TypeRegistry.NEAR, c2.getCondition());
        assertEquals(TypeRegistry.WARNING, c2.getParameter());

        Condition c3 = r.getConditions().get(2);
        assertEquals(TypeRegistry.NEAR, c3.getCondition());
        assertEquals(TypeRegistry.CHECK, c3.getParameter());
    }

    @Test
    void testMultipleVerb() {
        SyntaxValidator validator = new SyntaxValidator();
        List<Entity> sentence = List.of(
                new Entity(TypeRegistry.TEXT_CHIP, 0, 0),
                new Entity(TypeRegistry.IS, 1, 0),
                new Entity(TypeRegistry.PUSH, 2, 0),
                new Entity(TypeRegistry.AND, 3, 0),
                new Entity(TypeRegistry.EXTEND, 4, 0),
                new Entity(TypeRegistry.TEXT_PAPER, 5, 0)
        );

        List<Rule> rules = validator.validate(List.of(sentence));
        assertEquals(2, rules.size());

        Rule r = rules.get(0);
        assertEquals(TypeRegistry.CHIP, r.getSubject());
        assertEquals(TypeRegistry.IS, r.getVerb());
        assertEquals(TypeRegistry.PUSH, r.getEffect());
        assertEquals(0, r.getConditions().size());

        Rule r2 = rules.get(1);
        assertEquals(TypeRegistry.CHIP, r2.getSubject());
        assertEquals(TypeRegistry.EXTEND, r2.getVerb());
        assertEquals(TypeRegistry.TEXT_PAPER, r2.getEffect());
        assertEquals(0, r2.getConditions().size());
    }

    @Test
    void testValidSubsentences() {
        SyntaxValidator validator = new SyntaxValidator();
        List<Entity> sentence = List.of(
                new Entity(TypeRegistry.TEXT_JAVA, 0, 0),
                new Entity(TypeRegistry.IS, 1, 0),
                new Entity(TypeRegistry.TEXT_FLAG, 2, 0),
                new Entity(TypeRegistry.AND, 3, 0),
                new Entity(TypeRegistry.AND, 4, 0)
        );

        List<Rule> rules = validator.validate(List.of(sentence));
        assertEquals(1, rules.size());

        Rule r = rules.get(0);
        assertEquals(TypeRegistry.JAVA, r.getSubject());
        assertEquals(TypeRegistry.IS, r.getVerb());
        assertEquals(TypeRegistry.TEXT_FLAG, r.getEffect());
        assertEquals(0, r.getConditions().size());
    }


    @Test
    void testComplicatedString() {
        SyntaxValidator validator = new SyntaxValidator();
        List<Entity> sentence = List.of(
                new Entity(TypeRegistry.TEXT_GIT, 0, 0),
                new Entity(TypeRegistry.IS, 1, 0),
                new Entity(TypeRegistry.TEXT_ERROR, 2, 0),
                new Entity(TypeRegistry.AND, 3, 0),
                new Entity(TypeRegistry.TEXT_PYTHON, 4, 0),
                new Entity(TypeRegistry.FACING, 5, 0),
                new Entity(TypeRegistry.TEXT_XORGATE, 6, 0),
                new Entity(TypeRegistry.HAS, 7, 0),
                new Entity(TypeRegistry.TEXT_ERROR, 8, 0)
        );

        List<Rule> rules = validator.validate(List.of(sentence));
        assertEquals(3, rules.size());

        System.out.println("Input entities:");
        sentence.forEach(e -> System.out.println(e + " -> " + e.getType().getTypeId()));

        Rule r1 = rules.get(0);
        assertEquals(TypeRegistry.GIT, r1.getSubject());
        assertEquals(TypeRegistry.IS, r1.getVerb());
        assertEquals(TypeRegistry.TEXT_ERROR, r1.getEffect());
        assertEquals(0, r1.getConditions().size());

        Rule r2 = rules.get(1);
        assertEquals(TypeRegistry.GIT, r2.getSubject());
        assertEquals(TypeRegistry.IS, r2.getVerb());
        assertEquals(TypeRegistry.TEXT_PYTHON, r2.getEffect());
        assertEquals(0, r2.getConditions().size());

        Rule r3 = rules.get(2);
        assertEquals(TypeRegistry.PYTHON, r3.getSubject());
        assertEquals(TypeRegistry.HAS, r3.getVerb());
        assertEquals(TypeRegistry.TEXT_ERROR, r3.getEffect());
        assertEquals(1, r3.getConditions().size());
    }

    @Test
    void testInvalidSentences() {
        SyntaxValidator validator = new SyntaxValidator();
        List<Entity> shortSentence = List.of(
                new Entity(TypeRegistry.TEXT_JAVA, 0, 0),
                new Entity(TypeRegistry.IS, 1, 0)
        );

        List<Rule> rules1 = validator.validate(List.of(shortSentence));
        assertTrue(rules1.isEmpty());

        List<Entity> gibberish = List.of(
                new Entity(TypeRegistry.AND, 0, 0),
                new Entity(TypeRegistry.IS, 1, 0),
                new Entity(TypeRegistry.FACING, 2, 0),
                new Entity(TypeRegistry.YOU, 3, 0),
                new Entity(TypeRegistry.HAS, 4, 0),
                new Entity(TypeRegistry.PUSH, 5, 0),
                new Entity(TypeRegistry.TEXT_PAPER, 6, 0),
                new Entity(TypeRegistry.TEXT_JAVA, 7, 0)
        );

        List<Rule> rules2 = validator.validate(List.of(gibberish));
        assertTrue(rules2.isEmpty());

        List<Entity> invalidSyntax = List.of(
                new Entity(TypeRegistry.TEXT_JAVA, 0, 0),
                new Entity(TypeRegistry.ON, 1, 0),
                new Entity(TypeRegistry.TEXT_CHECK, 2, 0),
                new Entity(TypeRegistry.AND, 3, 0),
                new Entity(TypeRegistry.NEAR, 4, 0),
                new Entity(TypeRegistry.TEXT_DATABASE, 5, 0),
                new Entity(TypeRegistry.IS, 6, 0),
                new Entity(TypeRegistry.FACING, 7, 0)
        );

        List<Rule> rules3 = validator.validate(List.of(invalidSyntax));
        assertTrue(rules3.isEmpty());
    }
}
