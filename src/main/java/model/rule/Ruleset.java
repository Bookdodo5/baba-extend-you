package model.rule;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.word.PropertyType;
import model.map.LevelMap;

import java.util.ArrayList;

public class Ruleset {
    private final ArrayList<Rule> rules;

    public Ruleset() {
        rules = new ArrayList<>();
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    public void reset() {
        rules.clear();
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }
}