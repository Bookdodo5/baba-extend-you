package logic.rule.evaluator;

import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Rule;
import model.rule.Ruleset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InheritanceResolverTest {
    private InheritanceResolver resolver;
    private LevelMap levelMap;
    private Ruleset ruleset;
    private Entity javaEntity;

    @BeforeEach
    void setUp() {
        resolver = new InheritanceResolver();
        levelMap = new LevelMap(10, 10);
        ruleset = new Ruleset();
        javaEntity = new Entity(TypeRegistry.JAVA);
        levelMap.setPosition(javaEntity, 5, 5);
    }

    @Test
    void testDirectTypeMatch() {
        assertTrue(resolver.isInstanceOf(javaEntity, TypeRegistry.JAVA, levelMap, ruleset));
    }

    @Test
    void testNoExtendRules() {
        assertFalse(resolver.isInstanceOf(javaEntity, TypeRegistry.DOCUMENT, levelMap, ruleset));
    }

    @Test
    void testSingleLevelInheritance() {
        // JAVA EXTEND DOCUMENT
        ruleset.setRules(List.of(new Rule(
                new Entity(TypeRegistry.TEXT_JAVA),
                new Entity(TypeRegistry.EXTEND),
                new Entity(TypeRegistry.TEXT_DOCUMENT),
                List.of()
        )));

        assertTrue(resolver.isInstanceOf(javaEntity, TypeRegistry.DOCUMENT, levelMap, ruleset));
        assertFalse(resolver.isInstanceOf(javaEntity, TypeRegistry.FLAG, levelMap, ruleset));
    }

    @Test
    void testMultiLevelInheritance() {
        // JAVA EXTEND DOCUMENT, DOCUMENT EXTEND FLAG
        ruleset.setRules(List.of(
                new Rule(
                        new Entity(TypeRegistry.TEXT_JAVA),
                        new Entity(TypeRegistry.EXTEND),
                        new Entity(TypeRegistry.TEXT_DOCUMENT),
                        List.of()
                ),
                new Rule(
                        new Entity(TypeRegistry.TEXT_DOCUMENT),
                        new Entity(TypeRegistry.EXTEND),
                        new Entity(TypeRegistry.TEXT_FLAG),
                        List.of()
                )
        ));

        assertTrue(resolver.isInstanceOf(javaEntity, TypeRegistry.DOCUMENT, levelMap, ruleset));
        assertTrue(resolver.isInstanceOf(javaEntity, TypeRegistry.FLAG, levelMap, ruleset));
    }

    @Test
    void testWrongDirection() {
        // DOCUMENT EXTEND JAVA
        ruleset.setRules(List.of(new Rule(
                new Entity(TypeRegistry.TEXT_DOCUMENT),
                new Entity(TypeRegistry.EXTEND),
                new Entity(TypeRegistry.TEXT_JAVA),
                List.of()
        )));

        assertFalse(resolver.isInstanceOf(javaEntity, TypeRegistry.DOCUMENT, levelMap, ruleset));
    }

    @Test
    void testCycle() {
        // JAVA EXTEND DOCUMENT, DOCUMENT EXTEND JAVA
        ruleset.setRules(List.of(
                new Rule(
                        new Entity(TypeRegistry.TEXT_JAVA),
                        new Entity(TypeRegistry.EXTEND),
                        new Entity(TypeRegistry.TEXT_DOCUMENT),
                        List.of()
                ),
                new Rule(
                        new Entity(TypeRegistry.TEXT_DOCUMENT),
                        new Entity(TypeRegistry.EXTEND),
                        new Entity(TypeRegistry.TEXT_JAVA),
                        List.of()
                )
        ));

        Entity documentEntity = new Entity(TypeRegistry.DOCUMENT);
        levelMap.setPosition(documentEntity, 1, 1);

        assertTrue(resolver.isInstanceOf(javaEntity, TypeRegistry.DOCUMENT, levelMap, ruleset));
        assertTrue(resolver.isInstanceOf(documentEntity, TypeRegistry.JAVA, levelMap, ruleset));
    }
}
