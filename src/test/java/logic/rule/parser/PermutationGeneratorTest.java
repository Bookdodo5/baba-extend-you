package logic.rule.parser;

import model.entity.Entity;
import model.entity.TypeRegistry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PermutationGeneratorTest {

    @Test
    void testSingleLine() {
        PermutationGenerator generator = new PermutationGenerator();

        List<List<List<Entity>>> textTiles = new ArrayList<>();
        List<List<Entity>> line = new ArrayList<>();
        line.add(List.of(new Entity(TypeRegistry.TEXT_JAVA, 0, 0)));
        line.add(List.of(new Entity(TypeRegistry.IS, 1, 0)));
        line.add(List.of(new Entity(TypeRegistry.YOU, 2, 0)));
        textTiles.add(line);

        List<List<Entity>> perms = generator.generate(textTiles);
        assertEquals(1, perms.size());
        List<Entity> perm = perms.get(0);
        assertEquals(3, perm.size());
        assertEquals(TypeRegistry.TEXT_JAVA, perm.get(0).getType());
        assertEquals(TypeRegistry.IS, perm.get(1).getType());
        assertEquals(TypeRegistry.YOU, perm.get(2).getType());
    }

    @Test
    void testStackedTiles() {
        PermutationGenerator generator = new PermutationGenerator();

        List<List<List<Entity>>> textTiles = new ArrayList<>();
        List<List<Entity>> line = new ArrayList<>();
        line.add(List.of(
                new Entity(TypeRegistry.TEXT_JAVA, 0, 0),
                new Entity(TypeRegistry.TEXT_PYTHON, 0, 0)
        ));
        line.add(List.of(new Entity(TypeRegistry.IS, 1, 0)));
        line.add(List.of(
                new Entity(TypeRegistry.YOU, 2, 0),
                new Entity(TypeRegistry.PUSH, 2, 0)
        ));
        textTiles.add(line);

        List<List<Entity>> perms = generator.generate(textTiles);
        assertEquals(4, perms.size());

        assertTrue(perms.stream().anyMatch(p -> p.get(0).getType() == TypeRegistry.TEXT_JAVA));
        assertTrue(perms.stream().anyMatch(p -> p.get(0).getType() == TypeRegistry.TEXT_PYTHON));
        assertTrue(perms.stream().anyMatch(p -> p.get(2).getType() == TypeRegistry.YOU));
        assertTrue(perms.stream().anyMatch(p -> p.get(2).getType() == TypeRegistry.PUSH));
        perms.forEach(p -> {
            assertEquals(TypeRegistry.IS, p.get(1).getType());
        });
    }

    @Test
    void testMultipleLines() {
        PermutationGenerator generator = new PermutationGenerator();

        List<List<List<Entity>>> textTiles = new ArrayList<>();

        // line 1: 2 permutations (A or B) x 1 x 1 = 2
        List<List<Entity>> line1 = new ArrayList<>();
        line1.add(List.of(new Entity(TypeRegistry.TEXT_JAVA, 0, 0), new Entity(TypeRegistry.TEXT_PYTHON, 0, 0)));
        line1.add(List.of(new Entity(TypeRegistry.ON, 1, 0)));
        line1.add(List.of(new Entity(TypeRegistry.TEXT_PAPER, 2, 0)));

        // line 2: 1 permutation
        List<List<Entity>> line2 = new ArrayList<>();
        line2.add(List.of(new Entity(TypeRegistry.IS, 0, 1)));
        line2.add(List.of(new Entity(TypeRegistry.PUSH, 1, 1)));
        line2.add(List.of(new Entity(TypeRegistry.YOU, 2, 1)));

        textTiles.add(line1);
        textTiles.add(line2);

        List<List<Entity>> perms = generator.generate(textTiles);
        // total permutations should be 2 + 1 = 3
        assertEquals(3, perms.size());
    }

    @Test
    void testEmptyInput() {
        PermutationGenerator generator = new PermutationGenerator();
        List<List<List<Entity>>> textTiles = new ArrayList<>();

        List<List<Entity>> perms = generator.generate(textTiles);
        assertTrue(perms.isEmpty());
    }
}
