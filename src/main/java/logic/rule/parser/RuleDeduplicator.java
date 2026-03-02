package logic.rule.parser;

import model.rule.Rule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class responsible for deduplicating rules in case of unintended duplicates.
 * <p>Example case: (AND*2, JAVA, IS, YOU) parses into two identical (JAVA, IS, YOU) rules.</p>
 */
public class RuleDeduplicator {

    /**
     * Removes duplicate rules from the list, keeping the first occurrence of each unique rule.
     *
     * @param rules the list of rules to deduplicate
     * @return a list with duplicate rules removed
     */
    public List<Rule> deduplicate(List<Rule> rules) {
        Set<String> seen = new HashSet<>();
        return rules.stream()
                .filter(rule -> checkSeen(rule, seen))
                .toList();
    }

    private boolean checkSeen(Rule rule, Set<String> seen) {
        String ruleSignature = rule.getEntitySignature();
        if(seen.contains(ruleSignature)) {
            return false;
        } else {
            seen.add(ruleSignature);
            return true;
        }
    }
}
