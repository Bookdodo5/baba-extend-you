package model.rule;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.word.EffectType;
import model.entity.word.NounType;
import model.entity.word.VerbType;

import java.util.List;

/**
 * Represents a rule consisting of a subject, verb, effect, and optional conditions.
 */
public class Rule {
    private final EntityType subject;
    private final VerbType verb;
    private final EntityType effect;
    private final Entity subjectText;
    private final Entity verbText;
    private final Entity effectText;
    private final List<Condition> conditions;

    public Rule(Entity subjectText, Entity verbText, Entity effectText, List<Condition> conditions) {
        this.subjectText = subjectText;
        this.verbText = verbText;
        this.effectText = effectText;
        this.conditions = conditions;
        this.subject = ((NounType) subjectText.getType()).getReferencedType();
        this.verb = (VerbType) verbText.getType();

        EffectType effectType = (EffectType) effectText.getType();
        if (effectType instanceof NounType nounType) {
            this.effect = nounType.getReferencedType();
        } else {
            this.effect = effectType;
        }
    }

    /**
     * Returns the entity type that this rule applies to (the subject).
     *
     * @return the subject entity type
     */
    public EntityType getSubject() {
        return subject;
    }

    /**
     * Returns the verb of this rule (IS, HAS, EXTEND).
     *
     * @return the verb type
     */
    public VerbType getVerb() {
        return verb;
    }

    /**
     * Returns the effect of this rule (a property type or an entity type).
     *
     * @return the effect entity type
     */
    public EntityType getEffect() {
        return effect;
    }

    /**
     * Returns the text entity representing the subject noun on the map.
     *
     * @return the subject text entity
     */
    public Entity getSubjectText() {
        return subjectText;
    }

    /**
     * Returns the text entity representing the verb on the map.
     *
     * @return the verb text entity
     */
    public Entity getVerbText() {
        return verbText;
    }

    /**
     * Returns the text entity representing the effect on the map.
     *
     * @return the effect text entity
     */
    public Entity getEffectText() {
        return effectText;
    }

    /**
     * Returns the list of conditions attached to this rule.
     *
     * @return the list of conditions
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * Returns a unique string signature based on the entity IDs of the subject, verb, effect, and conditions.
     * Used for deduplication and equality checks.
     *
     * @return the entity-based signature string
     */
    public String getEntitySignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(subjectText.getEntityId());
        sb.append(",");
        sb.append(verbText.getEntityId());
        sb.append(",");
        sb.append(effectText.getEntityId());
        sb.append(",");
        conditions.forEach(c -> {
            sb.append(c.getConditionText().getEntityId());
            sb.append(":");
            sb.append(c.getParameterText().getEntityId());
            sb.append(";");
        });
        return sb.toString();
    }

    public String toString() {
        StringBuilder ruleText = new StringBuilder();

        ruleText.append(getSubject().getTypeId());
        ruleText.append(" ");
        for (Condition condition : getConditions()) {
            ruleText.append("(");
            ruleText.append(condition.getCondition().getTypeId());
            ruleText.append(" ");
            ruleText.append(condition.getParameter().getTypeId());
            ruleText.append(") ");
        }
        ruleText.append(getVerb().getTypeId());
        ruleText.append(" ");
        ruleText.append(getEffect().getTypeId());
        return ruleText.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rule other)) {
            return false;
        }
        return getEntitySignature().equals(other.getEntitySignature());
    }

    @Override
    public int hashCode() {
        return getEntitySignature().hashCode();
    }
}
