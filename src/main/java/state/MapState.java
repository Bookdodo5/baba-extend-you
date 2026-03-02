package state;

import application.GameController;
import application.Audio;
import javafx.scene.canvas.GraphicsContext;
import logic.input.InputCommand;
import logic.input.InputUtility;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelLoader;
import model.map.LevelMap;
import utils.GraphicUtils;
import utils.ImageUtils;

import java.awt.*;
import java.io.InputStream;
import java.util.*;

import static application.Constant.*;

public class MapState implements GameState {

    private final LevelMap levelSelectorMap;

    private final Map<Point, String> LEVEL_POSITION_FILENAME;

    private final Point cursorPos;

    public MapState() {
        levelSelectorMap = LevelLoader.loadLevel("levelSelector/MAP.csv");
        LEVEL_POSITION_FILENAME = new HashMap<>();
        loadLevelPositionFilename();
        cursorPos = new Point(1, 1);
    }

    private void loadLevelPositionFilename() {
        InputStream inputStream = MapState.class.getClassLoader().getResourceAsStream("levelSelector/LEVEL_POSITION.csv");
        if (inputStream == null) {
            return;
        }

        try (Scanner myReader = new Scanner(inputStream)) {
            while (myReader.hasNextLine()) {
                String[] parts = myReader.nextLine().trim().split(",");
                try {
                    int x = Integer.parseInt(parts[1].trim());
                    int y = Integer.parseInt(parts[2].trim());
                    LEVEL_POSITION_FILENAME.put(new Point(x, y), parts[0]);
                } catch (NumberFormatException e) {
                    // skip malformed line
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void onEnter(GameStateEnum previousState) {
    }

    @Override
    public void onExit() {
    }

    /**
     *
     */
    @Override
    public void update() {
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
        String levelFile = LEVEL_POSITION_FILENAME.get(cursorPos);
        if (levelFile == null) {
            return;
        }
        GameController.getInstance().playLevel("map/" + levelFile);
    }

    private void handleMoveRight() {
        if (cursorPos.x >= levelSelectorMap.getWidth() - 1) {
            return;
        }
        for (Entity e : levelSelectorMap.getEntitiesAt(cursorPos.x + 1, cursorPos.y)) {
            if (e.getType() == TypeRegistry.WIRE) {
                cursorPos.x++;
                Audio.playSfx("sound/SFX/select.wav");
                return;
            }
        }
    }

    private void handleMoveLeft() {
        if (cursorPos.x <= 0) {
            return;
        }
        for (Entity e : levelSelectorMap.getEntitiesAt(cursorPos.x - 1, cursorPos.y)) {
            if (e.getType() == TypeRegistry.WIRE) {
                cursorPos.x--;
                Audio.playSfx("sound/SFX/select.wav");
                return;
            }
        }
    }

    private void handleMoveDown() {
        if (cursorPos.y >= levelSelectorMap.getHeight() - 1) {
            return;
        }
        for (Entity e : levelSelectorMap.getEntitiesAt(cursorPos.x, cursorPos.y + 1)) {
            if (e.getType() == TypeRegistry.WIRE) {
                cursorPos.y++;
                Audio.playSfx("sound/SFX/select.wav");
                return;
            }
        }
    }

    private void handleMoveUp() {
        if (cursorPos.y <= 0) {
            return;
        }
        for (Entity e : levelSelectorMap.getEntitiesAt(cursorPos.x, cursorPos.y - 1)) {
            if (e.getType() == TypeRegistry.WIRE) {
                cursorPos.y--;
                Audio.playSfx("sound/SFX/select.wav");
                return;
            }
        }
    }

    /**
     *
     */
    @Override
    public void render(GraphicsContext gc) {
        int xOffset = (int) (gc.getCanvas().getWidth() / 2) - (levelSelectorMap.getWidth() * SPRITE_SIZE) / 2;
        int yOffset = (int) (gc.getCanvas().getHeight() / 2) - (levelSelectorMap.getHeight() * SPRITE_SIZE) / 2;
        Point offset = new Point(xOffset, yOffset);

        GraphicUtils.renderBackground(
                gc, offset,
                SPRITE_SIZE * levelSelectorMap.getWidth(),
                SPRITE_SIZE * levelSelectorMap.getHeight()
        );

        GraphicUtils.renderEntities(
                gc, levelSelectorMap, offset,
                null, null
        );

        // Draw the player cursor on top of the map entities
        long currentTime = System.currentTimeMillis();
        int animationFrame = (int) ((currentTime / MILLISECONDS_PER_FRAME) % WOBBLE_FRAME_COUNT);
        ImageUtils.drawSprite(
                gc, TypeRegistry.JAVA.getSpriteSheet(),
                animationFrame, 0,
                SPRITE_SIZE * cursorPos.x + offset.x,
                SPRITE_SIZE * cursorPos.y + offset.y
        );
    }
}
