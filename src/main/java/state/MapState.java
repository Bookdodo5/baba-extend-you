package state;

import application.GameController;
import application.Audio;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.input.InputCommand;
import logic.input.InputUtility;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;
import model.map.LevelLoader;
import model.map.LevelMap;
import utils.ImageUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

import static application.Constant.*;

public class MapState implements GameState {

    private LevelMap levelSelectorMap;

    private final Map<Integer, String> FILE_NAME;

    private static final Point[] SURROUNDING_DIRECTIONS = {
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0)
    };

    private Point cursorPos;

    public MapState(){
        levelSelectorMap = LevelLoader.loadLevel("map/MAP.csv");
        FILE_NAME = new HashMap<>();
        FILE_NAME.put(1, "JAVA_IS_YOU");
        FILE_NAME.put(2, "WHERE_DO_I_GO");
        FILE_NAME.put(3, "NOW_WHAT_IS_THIS");
        FILE_NAME.put(4, "OUT_OF_REACH");
        FILE_NAME.put(5, "SUMMER_IN_THAILAND");
        FILE_NAME.put(6, "ENCAPSULATED");
        FILE_NAME.put(7, "CROSSING_RIVER");
        FILE_NAME.put(8, "INHERITANCE");
        FILE_NAME.put(9, "LOGIC_GATE");
        FILE_NAME.put(10, "FETCH_DECODE_EXECUTE");
        FILE_NAME.put(11, "SHIFT_REGISTER");
        FILE_NAME.put(12, "STATIC_HAZARD");
        FILE_NAME.put(13, "PROPAGATION_DELAY");
        FILE_NAME.put(14, "SPEED_UP");
        FILE_NAME.put(15, "TUNNEL_SPAGHETTI");
        FILE_NAME.put(16, "CONDITIONALS");
        FILE_NAME.put(17, "LOOP");
        FILE_NAME.put(18, "NESTED_LOOP");
        FILE_NAME.put(19, "DONT_TOUCH_IF_IT_WORKS");
        FILE_NAME.put(20, "TRAVELLING_SALESMAN");
        FILE_NAME.put(21, "LINKED_LIST");
        FILE_NAME.put(22, "DURABILITY");
        FILE_NAME.put(23, "HORIZONTAL_SCALING");
        FILE_NAME.put(24, "TERNARY_RELATIONSHIP");
        FILE_NAME.put(25, "REDUNDANCY");
        FILE_NAME.put(26, "TRIGGER");
        FILE_NAME.put(27, "STORED_PROCEDURE");
        FILE_NAME.put(28, "JAR");
        FILE_NAME.put(29, "GIT_REBASE");
        FILE_NAME.put(30, "STACK_PANE");
        FILE_NAME.put(31, "JAVA_IS_WIN");
        cursorPos = new Point(1, 1);
    }

    @Override
    public void onEnter(GameStateEnum previousState) {
    }

    @Override
    public void onExit() {
        // Code here happens when exiting the level selector
    }

    /**
     *
     */
    @Override
    public void update() {
        System.out.println(cursorPos.x + " " + cursorPos.y);

        InputCommand input = InputUtility.getTriggered();
        switch (input) {
            case MOVE_UP -> handleMoveUp();
            case MOVE_DOWN -> handleMoveDown();
            case MOVE_LEFT -> handleMoveLeft();
            case MOVE_RIGHT -> handleMoveRight();
            case TRIGGER -> handleTrigger();
        }
    }

    private void handleTrigger() {
        // TODO (MAP) : handle level selection with GameController.getInstance().playLevel(....); RETURN IF NOT POSSIBLE.
        //GameController.getInstance().playLevel("map/" + 1 + "_" + FILE_NAME.get(1) + ".csv");
        for(Entity e: levelSelectorMap.getEntitiesAt(cursorPos)){
            for(int i=1; i<=31; i++){
                if(e.getType().getTypeId().equals(Integer.toString(i))){
                    GameController.getInstance().playLevel("map/" + i + "_" + FILE_NAME.get(i) + ".csv");
                }
            }
        }
    }

    private void handleMoveRight() {
        if(cursorPos.x >= levelSelectorMap.getWidth() - 1) return;
        for(Entity e: levelSelectorMap.getEntitiesAt(cursorPos.x+1, cursorPos.y)){
            if(e.getType() == TypeRegistry.WIRE){
                cursorPos.x++;
                Audio.playSfx("sound/SFX/select.wav");
            }
        }
    }

    private void handleMoveLeft() {
        if(cursorPos.x <= 0) return;
        for(Entity e: levelSelectorMap.getEntitiesAt(cursorPos.x-1, cursorPos.y)){
            if(e.getType() == TypeRegistry.WIRE){
                cursorPos.x--;
                Audio.playSfx("sound/SFX/select.wav");
            }
        }
    }

    private void handleMoveDown() {
        if(cursorPos.y >= levelSelectorMap.getHeight() - 1) return;
        for(Entity e: levelSelectorMap.getEntitiesAt(cursorPos.x, cursorPos.y+1)){
            if(e.getType() == TypeRegistry.WIRE){
                cursorPos.y++;
                Audio.playSfx("sound/SFX/select.wav");
            }
        }
    }

    private void handleMoveUp() {
        if(cursorPos.y <= 0) return;
        for(Entity e: levelSelectorMap.getEntitiesAt(cursorPos.x, cursorPos.y-1)){
            if(e.getType() == TypeRegistry.WIRE){
                cursorPos.y--;
                Audio.playSfx("sound/SFX/select.wav");
            }
        }
    }

    /**
     *
     */
    @Override
    public void render(GraphicsContext gc) {
        // Paints the level selector map
        // Consider the current cursor position

        gc.setFill(javafx.scene.paint.Color.rgb(0, 100, 100, 0.5));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        int xOffset = (int) (gc.getCanvas().getWidth() / 2) - (levelSelectorMap.getWidth() * SPRITE_SIZE) / 2;
        int yOffset = (int) (gc.getCanvas().getHeight() / 2) - (levelSelectorMap.getHeight() * SPRITE_SIZE) / 2;
        Point offset = new Point(xOffset, yOffset);

        renderEntities(gc, offset);

    }

    private void renderEntities(GraphicsContext gc, Point offset) {

        long currentTime = System.currentTimeMillis();
        int animationFrame = (int) ((currentTime / MILLISECONDS_PER_FRAME) % WOBBLE_FRAME_COUNT);

        List<Entity> entities = levelSelectorMap.getEntities().stream()
                .sorted(Comparator.comparingInt(e -> e.getType().getZIndex()))
                .toList();

        for (Entity entity : entities) {
            EntityType entityType = entity.getType();
            Image image = entityType.getSpriteSheet();

            int gridX = levelSelectorMap.getX(entity);
            int gridY = levelSelectorMap.getY(entity);
            int drawX = SPRITE_SIZE * gridX + offset.x;
            int drawY = SPRITE_SIZE * gridY + offset.y;

            int spriteRow = switch (entityType.getAnimationStyle()) {
                case WOBBLE -> 0;
                case TILED -> getSurroundingNumber(entity, levelSelectorMap);
                case DIRECTIONAL -> getDirectionalNumber(entity);
            };

            ImageUtils.drawSprite(gc, image, animationFrame, spriteRow, drawX, drawY);
            gc.setEffect(null);
        }
        ImageUtils.drawSprite(gc, new Image("sprite/JAVA.png"), animationFrame, 0, SPRITE_SIZE * cursorPos.x + offset.x, SPRITE_SIZE * cursorPos.y + offset.y);
    }

    private int getSurroundingNumber(Entity entity, LevelMap levelMap) {
        int surroundingNumber = 0;
        for (int direction = 0; direction < 4; direction++) {
            List<Entity> surroundingEntities = levelMap.getEntitiesAt(
                    levelMap.getX(entity) + SURROUNDING_DIRECTIONS[direction].x,
                    levelMap.getY(entity) + SURROUNDING_DIRECTIONS[direction].y
            );
            boolean hasSurroundingInDirection = surroundingEntities.stream()
                    .anyMatch(e -> e.getType() == entity.getType());
            if (hasSurroundingInDirection) {
                surroundingNumber += (1 << direction);
            }
        }
        return surroundingNumber;
    }

    private int getDirectionalNumber(Entity entity) {
        return switch (entity.getDirection()) {
            case UP -> 0;
            case RIGHT -> 1;
            case LEFT -> 2;
            case DOWN -> 3;
        };
    }

}
