package logic;

import logic.input.InputCommand;
import logic.input.InputUtility;
import logic.rule.evaluator.RuleEvaluator;
import logic.rule.parser.RuleParser;
import model.action.ActionStack;
import model.entity.Direction;
import model.map.LevelMap;
import model.rule.Rule;
import model.rule.Ruleset;

import java.util.List;

import static application.Constant.INPUT_COOLDOWN_MILLIS;

public class GameController {
    private LevelMap levelMap;
    private LevelMap levelMapPrototype;
    private final Ruleset ruleset = new Ruleset();
    private final RuleParser ruleParser = new RuleParser();
    private final RuleEvaluator ruleEvaluator = new RuleEvaluator();
    private final ActionStack actionStack = new ActionStack();

    private long lastInputTime = 0L;

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
            case MENU -> handleMenu();
            case WAIT -> processTurn();
            case MOVE_UP -> processMove(Direction.UP);
            case MOVE_DOWN -> processMove(Direction.DOWN);
            case MOVE_LEFT -> processMove(Direction.LEFT);
            case MOVE_RIGHT -> processMove(Direction.RIGHT);
        }
        lastInputTime = System.currentTimeMillis();
    }

    private void handleUndo() {
        System.out.println("Undo action triggered");
        boolean needReparse = actionStack.undo();
        if(needReparse) {
            parseRules();
        }
    }

    private void handleRedo() {
        System.out.println("Redo action triggered");
        boolean needReparse = actionStack.redo();
        if(needReparse) {
            parseRules();
        }
    }

    private void handleReset() {
        System.out.println("Reset action triggered");
        levelMap = new LevelMap(levelMapPrototype);
        actionStack.clear();
        parseRules();
    }

    private void handleMenu() {
        System.exit(0);
    }

    private void parseRules() {
        ruleset.reset();
        List<Rule> parsedRules = ruleParser.parseRules(levelMap);
        ruleset.addRules(parsedRules);
    }

    public void setLevelMap(LevelMap levelMap) {
        this.levelMap = levelMap;
        this.levelMapPrototype = new LevelMap(levelMap);
        parseRules();
    }

    public void processTurn() {
        // TODO: implement turn processing logic
        System.out.println("Processing turn...");
    }

    public void processMove(Direction direction) {
        // TODO: implement movement logic
        System.out.println("Processing move: " + direction);

        processTurn();
    }
}
