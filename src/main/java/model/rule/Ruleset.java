package model.rule;

import application.Music;
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

    public void setRules(List<Rule> ruleList) {
        if(ruleList.equals(rules)) {
            return;
        }
        if(ruleList.size() >= rules.size()) {
            Music.play("sound/SFX/comfirm.wav");
        }
        if(ruleList.size() <= rules.size()) {
            Music.play("sound/SFX/negative.wav");
        }
        reset();
        rules.addAll(ruleList);
    }

    public void reset() {
        rules.clear();
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

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