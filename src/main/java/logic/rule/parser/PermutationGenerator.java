package logic.rule.parser;

import model.entity.Entity;

import java.util.ArrayList;
import java.util.List;


/**
 * Generates all possible permutations of the rules, in case of overlapping text.
 */
public class PermutationGenerator {

    /**
     * Generates all entity-sequence permutations from a list of word-tile groups.
     *
     * @param textTiles list of word-tile groups, each group being a list of tile stacks
     * @return list of all permuted entity sequences (one per possible combination)
     */
    public List<List<Entity>> generate(List<List<List<Entity>>> textTiles) {
        List<List<Entity>> result = new ArrayList<>();

        for(List<List<Entity>> line : textTiles) {
            generateRecursive(result, line, new ArrayList<>());
        }

        return result;
    }

    /**
     * Recursively builds permutations by picking one entity from each tile slot.
     *
     * @param result         the accumulator to add completed sequences to
     * @param processingLine the current tile-group being permuted
     * @param current        the sequence built so far in the current recursion branch
     */
    private void generateRecursive(List<List<Entity>> result, List<List<Entity>> processingLine, List<Entity> current) {
        if(processingLine.size() == current.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        int nextIndex = current.size();
        List<Entity> nextEntities = processingLine.get(nextIndex);
        for(Entity nextEntity : nextEntities) {
            current.add(nextEntity);
            generateRecursive(result, processingLine, current);
            current.removeLast();
        }
    }
}
