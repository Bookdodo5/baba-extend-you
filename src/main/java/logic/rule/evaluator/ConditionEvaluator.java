package logic.rule.evaluator;

import logic.rule.evaluator.conditionCheckers.FacingChecker;
import logic.rule.evaluator.conditionCheckers.ConditionChecker;
import logic.rule.evaluator.conditionCheckers.InstanceofChecker;
import logic.rule.evaluator.conditionCheckers.NearChecker;
import logic.rule.evaluator.conditionCheckers.OnChecker;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Condition;
import model.rule.Ruleset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a list of conditions against a given entity within a level map and ruleset.
 */
public class ConditionEvaluator {
    private final Map<EntityType, ConditionChecker> conditionCheckers = new HashMap<>();

    public ConditionEvaluator() {
        conditionCheckers.put(TypeRegistry.ON, new OnChecker());
        conditionCheckers.put(TypeRegistry.FACING, new FacingChecker());
        conditionCheckers.put(TypeRegistry.NEAR, new NearChecker());
        conditionCheckers.put(TypeRegistry.INSTANCEOF, new InstanceofChecker());
    }

    /**
     * Returns {@code true} if all conditions in the list are satisfied for the given entity.
     *
     * @param entity     the entity to evaluate conditions against
     * @param conditions the list of conditions to check
     * @param levelMap   the current level map
     * @param ruleset    the active ruleset
     * @return {@code true} if every condition is satisfied
     */
    public boolean evaluate(Entity entity, List<Condition> conditions, LevelMap levelMap, Ruleset ruleset) {
        for (Condition condition : conditions) {
            ConditionChecker checker = conditionCheckers.get(condition.getCondition());
            if (checker == null || !checker.isSatisfied(entity, condition, levelMap, ruleset)) {
                return false;
            }
        }
        return true;
    }
}
