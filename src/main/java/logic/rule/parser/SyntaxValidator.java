package logic.rule.parser;

import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.entity.word.ConditionType;
import model.entity.word.EffectType;
import model.entity.word.NounType;
import model.entity.word.VerbType;
import model.rule.Condition;
import model.rule.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates the syntax of rule candidates using a finite state machine.
 */
public class SyntaxValidator {

    private enum State {
        START,
        SUBJECT,
        SUBJECT_AND,
        CONDITION,
        CONDITION_NOUN,
        CONDITION_AND,
        VERB,
        EFFECT,
        EFFECT_AND
    }

    private static class StateMachineContext {
        private final List<Rule> rules = new ArrayList<>();
        private final List<Entity> subjects = new ArrayList<>();
        private final List<Condition> conditions = new ArrayList<>();
        private Entity verb = null;
        private Entity conditionOp = null;
        private final List<Entity> effects = new ArrayList<>();
        private final List<Entity> sentence;
        private int tokenIdx = 0;
        private State state = State.START;

        public StateMachineContext(List<Entity> sentence) {
            this.sentence = sentence;
        }

        boolean hasMoreTokens() {
            return tokenIdx < sentence.size();
        }

        Entity currentToken() {
            return sentence.get(tokenIdx);
        }

        void advance(State nextState) {
            this.state = nextState;
            this.tokenIdx++;
        }

        void tryCreateRule() {
            if (verb == null || subjects.isEmpty() || effects.isEmpty()) {
                return;
            }
            for (Entity subject : subjects) {
                for (Entity effect : effects) {
                    Rule newRule = new Rule(subject, verb, effect, new ArrayList<>(conditions));
                    rules.add(newRule);
                }
            }
        }

        void reset() {
            subjects.clear();
            conditions.clear();
            effects.clear();
            verb = null;
            conditionOp = null;
            state = State.START;
        }

        void onInvalid() {
            reset();

            EntityType failedType = currentToken().getType();
            if (failedType instanceof NounType) {
                return;
            }

            boolean canFollowNoun = failedType instanceof VerbType
                    || failedType instanceof ConditionType
                    || failedType == TypeRegistry.AND;

            boolean canBacktrack = canFollowNoun
                    && tokenIdx > 0
                    && sentence.get(tokenIdx - 1).getType() instanceof NounType;

            if (canBacktrack) {
                tokenIdx--;
            } else {
                tokenIdx++;
            }
        }
    }

    /**
     * Validates all candidate entity sequences and returns the rules that pass syntax checks.
     *
     * @param ruleCandidates list of entity sequences to validate
     * @return list of syntactically valid {@link Rule} objects
     */
    public List<Rule> validate(List<List<Entity>> ruleCandidates) {
        List<Rule> allRules = new ArrayList<>();
        for (List<Entity> sentence : ruleCandidates) {
            StateMachineContext context = new StateMachineContext(sentence);
            runStateMachine(context);
            allRules.addAll(context.rules);
        }
        return allRules;
    }

    private void runStateMachine(StateMachineContext context) {
        while (context.hasMoreTokens()) {
            Entity token = context.currentToken();
            boolean validTransition = switch (context.state) {
                case START -> handleStart(context, token);
                case SUBJECT -> handleSubject(context, token);
                case SUBJECT_AND -> handleSubjectAnd(context, token);
                case CONDITION -> handleCondition(context, token);
                case CONDITION_NOUN -> handleConditionNoun(context, token);
                case CONDITION_AND -> handleConditionAnd(context, token);
                case VERB -> handleVerb(context, token);
                case EFFECT -> handleEffect(context, token);
                case EFFECT_AND -> handleEffectAnd(context, token);
            };

            if (!validTransition) {
                context.tryCreateRule();
                context.onInvalid();
            }
        }
        context.tryCreateRule();
    }

    private boolean handleStart(StateMachineContext context, Entity token) {
        if (token.getType() instanceof NounType) {
            context.subjects.add(token);
            context.advance(State.SUBJECT);
            return true;
        }
        return false;
    }

    private boolean handleSubject(StateMachineContext context, Entity token) {
        if (token.getType() instanceof ConditionType) {
            context.conditionOp = token;
            context.advance(State.CONDITION);
            return true;
        }
        if (token.getType() instanceof VerbType) {
            context.verb = token;
            context.advance(State.VERB);
            return true;
        }
        if (token.getType() == TypeRegistry.AND) {
            context.advance(State.SUBJECT_AND);
            return true;
        }
        return false;
    }

    private boolean handleSubjectAnd(StateMachineContext context, Entity token) {
        if (token.getType() instanceof NounType) {
            context.subjects.add(token);
            context.advance(State.SUBJECT);
            return true;
        }
        return false;
    }

    private boolean handleCondition(StateMachineContext context, Entity token) {
        if (token.getType() instanceof NounType) {
            context.conditions.add(new Condition(context.conditionOp, token));
            context.advance(State.CONDITION_NOUN);
            return true;
        }
        return false;
    }

    private boolean handleConditionNoun(StateMachineContext context, Entity token) {
        if (token.getType() instanceof VerbType) {
            context.verb = token;
            context.advance(State.VERB);
            return true;
        }
        if (token.getType() == TypeRegistry.AND) {
            context.advance(State.CONDITION_AND);
            return true;
        }
        return false;
    }

    private boolean handleConditionAnd(StateMachineContext context, Entity token) {
        if (token.getType() instanceof NounType) {
            context.conditions.add(new Condition(context.conditionOp, token));
            context.advance(State.CONDITION_NOUN);
            return true;
        }
        if (token.getType() instanceof ConditionType) {
            context.conditionOp = token;
            context.advance(State.CONDITION);
            return true;
        }
        return false;
    }

    private boolean handleVerb(StateMachineContext context, Entity token) {
        if (token.getType() instanceof EffectType) {
            context.effects.add(token);
            context.advance(State.EFFECT);
            return true;
        }
        return false;
    }

    private boolean handleEffect(StateMachineContext context, Entity token) {
        if (token.getType() == TypeRegistry.AND) {
            context.advance(State.EFFECT_AND);
            return true;
        }
        return false;
    }

    private boolean handleEffectAnd(StateMachineContext context, Entity token) {
        if (token.getType() instanceof EffectType) {
            context.effects.add(token);
            context.advance(State.EFFECT);
            return true;
        }
        if (token.getType() instanceof VerbType) {
            context.tryCreateRule();
            context.verb = token;
            context.effects.clear();
            context.advance(State.VERB);
            return true;
        }
        return false;
    }
}
