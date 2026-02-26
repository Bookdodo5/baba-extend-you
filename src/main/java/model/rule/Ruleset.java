package model.rule;

import application.Audio;
import model.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a collection of rules.
 */
public class Ruleset {
    private final ArrayList<Rule> rules;

    public Ruleset() {
        rules = new ArrayList<>();
    }

    /**
     * Replaces the current rule list with the given one, playing a sound to indicate growth or shrinkage.
     * Does nothing if the new list equals the current one.
     *
     * @param ruleList the new list of rules
     */
    public void setRules(List<Rule> ruleList) {
        if(ruleList.equals(rules)) {
            return;
        }
        else if(ruleList.size() >= rules.size()) {
            Audio.playSfx("sound/SFX/confirm.wav");
        }
        else {
            Audio.playSfx("sound/SFX/negative.wav");
        }
        rules.clear();
        rules.addAll(ruleList);
    }

    /**
     * Returns the current list of active rules.
     *
     * @return the list of rules
     */
    public ArrayList<Rule> getRules() {
        return rules;
    }

    /**
     * Returns the set of all text entities that are part of at least one active rule.
     * Used to visually distinguish active from inactive text tiles.
     *
     * @return the set of active text entities
     */
    public Set<Entity> getActiveTexts() {
        Set<Entity> activeEntities = new HashSet<>();
        for (Rule rule : rules) {
            activeEntities.add(rule.getEffectText());
            activeEntities.add(rule.getSubjectText());
            activeEntities.add(rule.getVerbText());
            for(Condition condition : rule.getConditions()) {
                activeEntities.add(condition.getConditionText());
                activeEntities.add(condition.getParameterText());
            }
        }
        return activeEntities;
    }
}