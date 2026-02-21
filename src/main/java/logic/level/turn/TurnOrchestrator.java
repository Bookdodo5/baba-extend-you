package logic.level.turn;

import application.GameController;
import application.Music;
import logic.rule.evaluator.RuleEvaluator;
import logic.rule.parser.RuleParser;
import model.action.CompositeAction;
import model.action.DestroyAction;
import model.action.MoveAction;
import model.entity.Direction;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.rule.Ruleset;

import java.util.List;

/**
 * Orchestrates the sequence of actions that occur during a game turn.
 *
 * <p>The method {@link #runTurn(Direction, LevelMap, Ruleset, RuleParser)} executes the turn after getting an input direction.</p>
 */
public class TurnOrchestrator {

    private final RuleEvaluator ruleEvaluator;
    private final CollisionResolver collisionResolver;
    private final InteractionHandler interactionHandler;
    private boolean winMusicPlaying;

    public TurnOrchestrator() {
        this.ruleEvaluator = new RuleEvaluator();
        this.collisionResolver = new CollisionResolver();
        this.interactionHandler = new InteractionHandler();
        this.winMusicPlaying = false;
    }

    public CompositeAction runTurn(Direction direction, LevelMap levelMap, Ruleset ruleset, RuleParser ruleParser) {
        // First pass: YOU intents
        List<MoveIntent> youIntents = getYouIntents(direction, levelMap, ruleset);
        CompositeAction youAction = collisionResolver.resolveCollisions(youIntents, levelMap, ruleset, ruleEvaluator);
        youAction.execute();

        // Second pass: MOVE intents
        List<MoveIntent> moveIntents = getMoveIntents(levelMap, ruleset);
        CompositeAction moveAction = collisionResolver.resolveCollisions(moveIntents, levelMap, ruleset, ruleEvaluator);
        moveAction.execute();

        // Reparse rules after both movement passes
        ruleset.setRules(ruleParser.parseRules(levelMap));

        // Handle interactions
        CompositeAction interactAction = interactionHandler.handleInteractions(levelMap, ruleset, ruleEvaluator);
        ruleset.setRules(ruleParser.parseRules(levelMap));
        interactAction.execute();

        // Check win
        if(ruleEvaluator.isWinConditionMet(levelMap, ruleset)) {
            GameController.getInstance().setCurrentLevelWin(true);
        }

        // Combine all actions
        youAction.combine(moveAction);
        youAction.combine(interactAction);

        // Play appropriate sounds
        if(youAction.getActions().stream()
                .anyMatch(action -> action instanceof MoveAction)) {
            Music.play("sound/SFX/moveElement.wav");
        }
        if(youAction.getActions().stream()
                .anyMatch(action -> action instanceof DestroyAction)
        ) {
            Music.play("sound/SFX/reset.wav");
        }
        if(levelMap.getEntities().stream()
                .noneMatch(entity -> ruleEvaluator.hasProperty(entity, TypeRegistry.YOU, levelMap, ruleset))
        ) {
            if (!winMusicPlaying) {
                Music.stopLoop();
                Music.playLoop("sound/SFX/win.wav");
                winMusicPlaying = true;
            }
        }
        else {
            if (winMusicPlaying) {
                Music.stopLoop();
                Music.playLoop("sound/music/Pixel_Quest_MainTheme.wav");
                winMusicPlaying = false;
            }
        }

        return youAction;
    }

    private List<MoveIntent> getYouIntents(Direction direction, LevelMap levelMap, Ruleset ruleset) {
        var entities = ruleEvaluator.getEntitiesWithProperty(TypeRegistry.YOU, levelMap, ruleset);
        return entities.stream()
                .map(entity -> new MoveIntent(entity, direction, false))
                .toList();
    }

    private List<MoveIntent> getMoveIntents(LevelMap levelMap, Ruleset ruleset) {
        return ruleset.getRules().stream()
                .filter(rule -> rule.getEffect() == TypeRegistry.MOVE)
                .filter(rule -> rule.getVerb() == TypeRegistry.IS)
                .flatMap(rule -> levelMap.getEntities().stream()
                        .filter(entity -> ruleEvaluator.hasPropertyFromRule(entity, rule, levelMap, ruleset))
                        .map(entity -> new MoveIntent(entity, entity.getDirection(), true)))
                .toList();
    }
}
