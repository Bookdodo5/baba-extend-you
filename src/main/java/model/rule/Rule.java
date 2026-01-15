package model.rule;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.word.EffectType;
import model.entity.word.VerbType;

import java.util.ArrayList;

public class Rule {
    private final EntityType subject;
    private final VerbType verb;
    private final EffectType effect;
    private final Entity subjectText;
    private final Entity verbText;
    private final Entity effectText;
    private final ArrayList<Condition> conditions;

    public Rule(Entity subjectText, Entity verbText, Entity effectText, ArrayList<Condition> conditions) {
        this.subjectText = subjectText;
        this.verbText = verbText;
        this.effectText = effectText;
        this.conditions = conditions;
        this.subject = subjectText.getType();
        this.verb = (VerbType)verbText.getType();
        this.effect = (EffectType)effectText.getType();
    }

    public EntityType getSubject() {
        return subject;
    }

    public VerbType getVerb() {
        return verb;
    }

    public EffectType getEffect() {
        return effect;
    }

    public Entity getSubjectText() {
        return subjectText;
    }

    public Entity getVerbText() {
        return verbText;
    }

    public Entity getPropertyText() {
        return effectText;
    }

    public ArrayList<Condition> getConditions() {
        return conditions;
    }
}
