package logic.rule.parser;

import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextScannerTest {
    private LevelMap levelMap;
    private TextScanner scanner;

    @BeforeEach
    void setUp() {
        levelMap = new LevelMap(10, 10);
        scanner = new TextScanner();
    }

    @Test
    void testHorizontalScan() {
        // place text words horizontally at y=0: (0,0),(1,0),(2,0)
        Entity java = new Entity(TypeRegistry.TEXT_JAVA);
        Entity is = new Entity(TypeRegistry.IS);
        Entity you = new Entity(TypeRegistry.YOU);
        levelMap.setEntityPosition(java, 0, 0);
        levelMap.setEntityPosition(is, 1, 0);
        levelMap.setEntityPosition(you, 2, 0);

        List<List<List<Entity>>> results = scanner.scanText(levelMap);
        // one horizontal sequence
        assertEquals(1, results.size(), "Expected exactly one sequence");
        List<List<Entity>> seq = results.get(0);
        assertEquals(3, seq.size(), "Sequence should contain three tiles");
        assertEquals(TypeRegistry.TEXT_JAVA, seq.get(0).get(0).getType());
        assertEquals(TypeRegistry.IS, seq.get(1).get(0).getType());
        assertEquals(TypeRegistry.YOU, seq.get(2).get(0).getType());
    }

    @Test
    void testVerticalScan() {
        // place text words vertically at x=4: (4,1),(4,2),(4,3)
        Entity rockText = new Entity(TypeRegistry.TEXT_PAPER);
        Entity is = new Entity(TypeRegistry.IS);
        Entity you = new Entity(TypeRegistry.YOU);
        levelMap.setEntityPosition(rockText, 4, 1);
        levelMap.setEntityPosition(is, 4, 2);
        levelMap.setEntityPosition(you, 4, 3);

        List<List<List<Entity>>> results = scanner.scanText(levelMap);
        // one vertical sequence
        assertEquals(1, results.size());
        List<List<Entity>> seq = results.get(0);
        assertEquals(3, seq.size());
        assertEquals(TypeRegistry.TEXT_PAPER, seq.get(0).get(0).getType());
        assertEquals(TypeRegistry.IS, seq.get(1).get(0).getType());
        assertEquals(TypeRegistry.YOU, seq.get(2).get(0).getType());
    }

    @Test
    void testStackedWords() {
        // tile (0,0) contains two word entities
        Entity java = new Entity(TypeRegistry.TEXT_JAVA);
        Entity python = new Entity(TypeRegistry.TEXT_PYTHON);
        Entity is = new Entity(TypeRegistry.IS);
        Entity you = new Entity(TypeRegistry.YOU);
        levelMap.setEntityPosition(java, 0, 0);
        levelMap.setEntityPosition(python, 0, 0);
        levelMap.setEntityPosition(is, 1, 0);
        levelMap.setEntityPosition(you, 2, 0);

        List<List<List<Entity>>> results = scanner.scanText(levelMap);
        assertEquals(1, results.size());
        List<List<Entity>> seq = results.get(0);
        assertEquals(3, seq.size());
        // first tile should contain both t1 and t2 (order not important)
        assertEquals(2, seq.get(0).size());
        assertTrue(seq.get(0).stream().anyMatch(e -> e.getType() == TypeRegistry.TEXT_JAVA));
        assertTrue(seq.get(0).stream().anyMatch(e -> e.getType() == TypeRegistry.TEXT_PYTHON));
        // following tiles single word each
        assertEquals(TypeRegistry.IS, seq.get(1).get(0).getType());
        assertEquals(TypeRegistry.YOU, seq.get(2).get(0).getType());
    }

    @Test
    void testShortSequences() {
        // only two consecutive words -> should be ignored
        Entity java = new Entity(TypeRegistry.TEXT_JAVA);
        Entity is = new Entity(TypeRegistry.IS);
        levelMap.setEntityPosition(java, 0, 5);
        levelMap.setEntityPosition(is, 1, 5);

        List<List<List<Entity>>> results = scanner.scanText(levelMap);
        assertTrue(results.isEmpty(), "Sequences shorter than 3 should be ignored");
    }

    @Test
    void testMultipleSequences() {
        // two horizontal sequences on different rows
        levelMap.setEntityPosition(new Entity(TypeRegistry.TEXT_JAVA), 0, 1);
        levelMap.setEntityPosition(new Entity(TypeRegistry.ON), 1, 1);
        levelMap.setEntityPosition(new Entity(TypeRegistry.TEXT_PAPER), 2, 1);
        levelMap.setEntityPosition(new Entity(TypeRegistry.IS), 3, 1);
        levelMap.setEntityPosition(new Entity(TypeRegistry.YOU), 4, 1);

        levelMap.setEntityPosition(new Entity(TypeRegistry.TEXT_KEY), 3, 0);
        levelMap.setEntityPosition(new Entity(TypeRegistry.SINK), 3, 2);

        levelMap.setEntityPosition(new Entity(TypeRegistry.TEXT_PAPER), 0, 2);
        levelMap.setEntityPosition(new Entity(TypeRegistry.IS), 1, 2);
        levelMap.setEntityPosition(new Entity(TypeRegistry.PUSH), 2, 2);

        List<List<List<Entity>>> results = scanner.scanText(levelMap);
        // expect two sequences (row 0 and row 2)
        assertEquals(3, results.size());
    }
}
