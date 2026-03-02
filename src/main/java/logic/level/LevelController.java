package logic.level;

import application.Audio;
import javafx.scene.paint.Color;
import logic.input.InputCommand;
import logic.level.turn.TurnOrchestrator;
import logic.input.InputUtility;
import logic.rule.evaluator.RuleEvaluator;
import logic.rule.parser.RuleParser;
import model.action.Action;
import model.action.ActionStack;
import model.action.CompositeAction;
import model.action.DestroyAction;
import model.action.MoveAction;
import model.entity.Direction;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelMap;
import model.particle.Particle;
import model.particle.ParticleType;
import model.rule.Rule;
import model.rule.Ruleset;
import state.PlayingState;

import java.awt.*;
import java.util.List;

import static application.Constant.INPUT_COOLDOWN_MILLIS;

/**
 * Manages the game level, processing player inputs, and updating the map state after the inputs.
 */
public class LevelController {
    private LevelMap levelMap;
    private LevelMap levelMapPrototype;
    private final Ruleset ruleset;
    private final RuleParser ruleParser;
    private final ActionStack actionStack;
    private final TurnOrchestrator turnOrchestrator;
    private final RuleEvaluator ruleEvaluator;

    private long lastInputTime = 0L;

    private boolean isLose = false;

    public LevelController() {
        this.ruleset = new Ruleset();
        this.ruleEvaluator = new RuleEvaluator();
        this.ruleParser = new RuleParser();
        this.actionStack = new ActionStack();
        this.turnOrchestrator = new TurnOrchestrator();
    }

    /**
     * Sets the level map to play and resets the action stack.
     *
     * @param levelMap the new level map
     */
    public void setLevelMap(LevelMap levelMap) {
        this.levelMap = levelMap;
        this.levelMapPrototype = new LevelMap(levelMap);
        actionStack.clear();
        parseRules();
    }

    /**
     * Returns the current level map.
     *
     * @return the active {@link LevelMap}
     */
    public LevelMap getLevelMap() {
        return levelMap;
    }

    /**
     * Returns the current ruleset parsed from the level map.
     *
     * @return the active {@link Ruleset}
     */
    public Ruleset getRuleset() {
        return ruleset;
    }

    /**
     * Processes input, updates particles, and advances the game state for one frame.
     *
     * @param playingState the current playing state (used for particle effects)
     */
    public void update(PlayingState playingState) {

        addPassiveParticles(playingState);

        long currentTime = System.currentTimeMillis();

        InputCommand triggered = InputUtility.getTriggered();
        InputCommand pressed = InputUtility.getPressed();

        if (triggered != InputCommand.NONE) {
            processInput(triggered, playingState);
            lastInputTime = currentTime;
        } else if (currentTime - lastInputTime >= INPUT_COOLDOWN_MILLIS && pressed != InputCommand.NONE) {
            processInput(pressed, playingState);
            lastInputTime = currentTime;
        }
    }

    /**
     *  Map each InputCommand to its corresponding function.
     * @param inputCommand
     * @param playingState
     */

    private void processInput(InputCommand inputCommand, PlayingState playingState) {
        switch (inputCommand) {
            case UNDO -> handleUndo();
            case REDO -> handleRedo();
            case RESET -> handleReset();
            case TRIGGER -> processTurn(null, playingState);
            case MOVE_UP -> processTurn(Direction.UP, playingState);
            case MOVE_DOWN -> processTurn(Direction.DOWN, playingState);
            case MOVE_LEFT -> processTurn(Direction.LEFT, playingState);
            case MOVE_RIGHT -> processTurn(Direction.RIGHT, playingState);
        }
        parseRules();
        handleLose();
    }

    /** Parses the current level map and updates the ruleset. */
    private void parseRules() {
        List<Rule> parsedRules = ruleParser.parseRules(levelMap);
        ruleset.setRules(parsedRules);
    }

    /** Undoes the last action and plays the undo sound. */
    private void handleUndo() {
        actionStack.undo();

        Audio.playSfx("sound/SFX/esc.wav");
    }

    /** Redoes the last undone action and plays the redo sound. */
    private void handleRedo() {
        actionStack.redo();

        Audio.playSfx("sound/SFX/esc.wav");
    }

    /** Resets the level to its initial state and plays the reset sound. */
    public void handleReset() {
        levelMap = new LevelMap(levelMapPrototype);
        actionStack.clear();

        Audio.playSfx("sound/SFX/reset.wav");
    }

    /**
     * Executes a game turn in the given direction and records the resulting composite action.
     *
     * @param direction    the direction of movement, or {@code null} for a stationary turn
     * @param playingState the current playing state (used for particle effects)
     */
    private void processTurn(Direction direction, PlayingState playingState) {
        CompositeAction actions = turnOrchestrator.runTurn(direction, levelMap, ruleset, ruleParser);
        if (actions.getActions().isEmpty()) {
            return;
        }

        if (actions.getActions().stream().anyMatch(action -> action instanceof MoveAction)) {
            Audio.playSfx("sound/SFX/moveElement.wav");
        }
        if (actions.getActions().stream().anyMatch(action -> action instanceof DestroyAction)) {
            Audio.playSfx("sound/SFX/destroy.wav");
        }

        addTurnParticles(actions, playingState);
        actionStack.newAction(actions);
    }

    /**
     * Emits move and destroy particles for the actions in the given composite action.
     *
     * @param actions      the composite action containing individual actions
     * @param playingState the playing state to add particles to
     */
    private void addTurnParticles(CompositeAction actions, PlayingState playingState) {
        for (Action action : actions.getActions()) {
            if (action instanceof MoveAction moveAction) {
                moveAction.addParticle(playingState);
            } else if (action instanceof DestroyAction destroyAction) {
                destroyAction.addParticle(playingState);
            }
        }
    }

    /**
     * Adds ambient particles to HOT and WIN entities each frame.
     *
     * @param playingState the playing state to add particles to
     */
    private void addPassiveParticles(PlayingState playingState) {
        for (Entity entity : ruleEvaluator.getEntitiesWithProperty(TypeRegistry.HOT, levelMap, ruleset)) {
            if (Math.random() < 0.002) {
                playingState.addParticle(new Particle(
                        levelMap.getX(entity) + (Math.random() - 0.5) / 2.0,
                        levelMap.getY(entity) + (Math.random() - 0.5) / 2.0,
                        (Math.random() - 0.5) / 1000.0,
                        (Math.random()) / 1000.0,
                        ParticleType.HOT,
                        Color.GRAY
                ));
            }
        }
        for (Entity entity : ruleEvaluator.getEntitiesWithProperty(TypeRegistry.WIN, levelMap, ruleset)) {
            if (Math.random() < 0.02) {
                playingState.addParticle(new Particle(
                        levelMap.getX(entity) + (Math.random() - 0.5) / 2.0,
                        levelMap.getY(entity) + (Math.random() - 0.5) / 2.0,
                        (Math.random() - 0.5) / 400.0,
                        (Math.random() - 0.5) / 400.0,
                        ParticleType.CROSS,
                        Color.LIGHTGOLDENRODYELLOW
                ));
            }
        }
    }

    /**
     * Spawns win particles at all positions where the win condition is currently met.
     *
     * @param playingState the playing state to add particles to
     */
    public void addWinParticle(PlayingState playingState) {
        for (Point point : ruleEvaluator.getWinConditionMetPositions(levelMap, ruleset)) {
            for (int i = 0; i < 20; i++) {
                playingState.addParticle(new Particle(
                        point.x + (Math.random() - 0.5) / 2.0,
                        point.y + (Math.random() - 0.5) / 2.0,
                        (Math.random() - 0.5) / 300.0,
                        (Math.random() - 0.5) / 300.0,
                        ParticleType.WIN,
                        Color.GOLD
                ));
            }
        }
    }

    /** Checks for a lose condition and pauses or resumes music accordingly. */
    private void handleLose() {
        boolean updatedIsLose = levelMap.getEntities().stream()
                .noneMatch(entity -> ruleEvaluator.hasProperty(entity, TypeRegistry.YOU, levelMap, ruleset));

        if (!updatedIsLose) {
            Audio.resumeMusic();
        } else if (!isLose) {
            Audio.pauseMusic();
            Audio.playSfx("sound/SFX/lose.wav");
        }

        isLose = updatedIsLose;
    }
}
