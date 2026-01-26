package logic.controller;

import logic.controller.turn.TurnOrchestrator;
import logic.input.InputCommand;
import logic.input.InputUtility;
import logic.rule.parser.RuleParser;
import model.action.ActionStack;
import model.action.CompositeAction;
import model.entity.Direction;
import model.map.LevelMap;
import model.rule.Rule;
import model.rule.Ruleset;

import java.util.List;

import static application.Constant.INPUT_COOLDOWN_MILLIS;

public class LevelController {
    private LevelMap levelMap;
    private LevelMap levelMapPrototype;
    private final Ruleset ruleset;
    private final RuleParser ruleParser;
    private final ActionStack actionStack;
    private final TurnOrchestrator turnOrchestrator;

    private long lastInputTime = 0L;

    public LevelController() {
        this.ruleset = new Ruleset();
        this.ruleParser = new RuleParser();
        this.actionStack = new ActionStack();
        this.turnOrchestrator = new TurnOrchestrator();
    }

    public void setLevelMap(LevelMap levelMap) {
        this.levelMap = levelMap;
        this.levelMapPrototype = new LevelMap(levelMap);
        parseRules();
    }

    public void update() {
        if(System.currentTimeMillis() - lastInputTime < INPUT_COOLDOWN_MILLIS) {
            return;
        }

        InputCommand playerInput = InputUtility.getInputCommand();
        switch (playerInput) {
            case NONE -> {
                return;
            }
            case UNDO -> handleUndo();
            case REDO -> handleRedo();
            case RESET -> handleReset();
            case MENU -> handlePause();
            case WAIT -> processTurn(null);
            case MOVE_UP -> processTurn(Direction.UP);
            case MOVE_DOWN -> processTurn(Direction.DOWN);
            case MOVE_LEFT -> processTurn(Direction.LEFT);
            case MOVE_RIGHT -> processTurn(Direction.RIGHT);
        }
        lastInputTime = System.currentTimeMillis();
        parseRules();
    }

    private void parseRules() {
        ruleset.reset();
        List<Rule> parsedRules = ruleParser.parseRules(levelMap);
        ruleset.setRules(parsedRules);
    }

    private void handleUndo() {
        System.out.println("Undo action triggered");
        actionStack.undo();
    }

    private void handleRedo() {
        System.out.println("Redo action triggered");
        actionStack.redo();
    }

    private void handleReset() {
        System.out.println("Reset action triggered");
        levelMap = new LevelMap(levelMapPrototype);
        GameController.getInstance().setCurrentMap(levelMap);
        actionStack.clear();
    }

    private void handlePause() {
        System.out.println("Pause action triggered");
        GameController.getInstance().pauseGame();
    }

    private void processTurn(Direction direction) {
        System.out.println("Processing turn with direction: " + direction);
        CompositeAction action = turnOrchestrator.runTurn(direction, levelMap, ruleset, ruleParser);
        actionStack.newAction(action);
    }
}
