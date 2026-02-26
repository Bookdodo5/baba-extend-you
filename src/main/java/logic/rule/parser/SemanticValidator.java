package logic.rule.parser;


import model.entity.word.NounType;
import model.entity.word.PropertyType;
import model.rule.Rule;

import java.util.List;

/**
 * Validates the semantic correctness of rules. Some rules are not possible even if the grammar is correct
 */
public class SemanticValidator {

    /**
     * Filters out rules that are semantically invalid (e.g. a HAS verb paired with a property effect).
     *
     * @param rules the list of syntactically valid rules to check
     * @return a list containing only semantically valid rules
     */
    public List<Rule> validate(List<Rule> rules) {
        return rules.stream()
                .filter(this::isSemanticallyValid)
                .toList();
    }

    private boolean isSemanticallyValid(Rule rule) {
        if(!rule.getVerb().acceptsNoun() && rule.getEffect() instanceof NounType) {
            return false;
        }
        if(!rule.getVerb().acceptsProperty() && rule.getEffect() instanceof PropertyType) {
            return false;
        }
        return true;
    }
}
