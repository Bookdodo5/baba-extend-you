package logic.rule.parser;

import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Rule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuleParserTest {

    @Test
    void testIntegration() {

        LevelMap levelMap = new LevelMap(10, 10);
        RuleParser ruleParser = new RuleParser();

        levelMap.addEntity(new Entity(TypeRegistry.TEXT_PAPER, 1, 0));
        levelMap.addEntity(new Entity(TypeRegistry.IS, 1, 1));
        levelMap.addEntity(new Entity(TypeRegistry.HOT, 1, 2));
        levelMap.addEntity(new Entity(TypeRegistry.TEXT_DOOR, 1, 3));
        levelMap.addEntity(new Entity(TypeRegistry.AND, 1, 3));
        levelMap.addEntity(new Entity(TypeRegistry.EXTEND, 1, 4));
        levelMap.addEntity(new Entity(TypeRegistry.MOVE, 1, 5));
        levelMap.addEntity(new Entity(TypeRegistry.TEXT_KEY, 1, 5));

        levelMap.addEntity(new Entity(TypeRegistry.TEXT_TILE, 3, 0));
        levelMap.addEntity(new Entity(TypeRegistry.AND, 3, 1));
        levelMap.addEntity(new Entity(TypeRegistry.TEXT_TILE, 3, 2));
        levelMap.addEntity(new Entity(TypeRegistry.TEXT_KEY, 3, 3));
        levelMap.addEntity(new Entity(TypeRegistry.TEXT_LAVA, 3, 4));
        levelMap.addEntity(new Entity(TypeRegistry.NEAR, 3, 5));

        levelMap.addEntity(new Entity(TypeRegistry.TEXT_JAVA, 0, 1));
        levelMap.addEntity(new Entity(TypeRegistry.YOU, 2, 1));
        levelMap.addEntity(new Entity(TypeRegistry.PUSH, 4, 1));

        levelMap.addEntity(new Entity(TypeRegistry.ON, 2, 3));
        levelMap.addEntity(new Entity(TypeRegistry.HAS, 4, 3));
        levelMap.addEntity(new Entity(TypeRegistry.TEXT_WATER, 5, 3));

        levelMap.addEntity(new Entity(TypeRegistry.TEXT_CHECK, 2, 5));
        levelMap.addEntity(new Entity(TypeRegistry.TEXT_WATER, 4, 5));
        levelMap.addEntity(new Entity(TypeRegistry.IS, 5, 5));
        levelMap.addEntity(new Entity(TypeRegistry.DEFEAT, 6, 5));
        levelMap.addEntity(new Entity(TypeRegistry.AND, 7, 5));
        levelMap.addEntity(new Entity(TypeRegistry.HAS, 8, 5));
        levelMap.addEntity(new Entity(TypeRegistry.MELT, 9, 5));

        levelMap.addEntity(new Entity(TypeRegistry.JAVA, 2, 2));
        levelMap.addEntity(new Entity(TypeRegistry.LAVA, 7, 5));
        levelMap.addEntity(new Entity(TypeRegistry.WATER, 8, 5));
        levelMap.addEntity(new Entity(TypeRegistry.TILE, 9, 5));

        List<Rule> results = ruleParser.parseRules(levelMap);

        assertEquals(8, results.size());

    }
}
