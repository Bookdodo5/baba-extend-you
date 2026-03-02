package state;

import application.GameController;
import application.Audio;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import logic.input.InputCommand;
import logic.input.InputUtility;
import model.entity.Entity;
import model.entity.TypeRegistry;
import model.map.LevelLoader;
import model.map.LevelMap;
import utils.GraphicUtils;

import java.awt.*;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static application.Constant.*;

public class MapState implements GameState {

    private final double LEVEL_NAME_SCALE = 1.5;
    private final double LEVEL_NUMBER_SCALE = 0.8;

    private static final ColorAdjust INCOMPLETE_LEVEL_EFFECT = new ColorAdjust();
    static {
        INCOMPLETE_LEVEL_EFFECT.setSaturation(0.1);
        INCOMPLETE_LEVEL_EFFECT.setBrightness(-0.3);
        INCOMPLETE_LEVEL_EFFECT.setHue(0.7);
    }

    private final Map<Point, String> LEVEL_POSITION_FILENAME;
    private final LevelMap levelSelectorMap;
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
                    LEVEL_POSITION_FILENAME.put(new Point(x, y), parts[0].trim());
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
        Audio.resumeMusic();
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
            case MENU -> handleMenu();
        }
    }

    private void handleMenu() {
        GameController.getInstance().setState(GameStateEnum.TITLE);
        Audio.playSfx("sound/SFX/esc.wav");
    }

    private void handleTrigger() {
        String levelFile = LEVEL_POSITION_FILENAME.get(cursorPos);
        if (levelFile != null) {
            GameController.getInstance().playLevel(levelFile);
        }
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

        renderEntities(gc, offset);
        renderLevelName(gc, offset);
        renderLevelNumber(gc, offset);
        renderCursor(gc, offset);
    }

    private void renderEntities(GraphicsContext gc, Point offset) {
        Set<Entity> incompleteLevels = levelSelectorMap.getEntities().stream()
                .filter(e -> e.getType() == TypeRegistry.TILE)
                .filter(e -> {
                    Point position = levelSelectorMap.getPosition(e);
                    String levelFileName = LEVEL_POSITION_FILENAME.get(position);
                    return !GameController.getInstance().isLevelCompleted(levelFileName);
                })
                .collect(Collectors.toSet());

        GraphicUtils.renderEntities(
                gc, levelSelectorMap, offset,
                incompleteLevels, INCOMPLETE_LEVEL_EFFECT
        );
    }

    private void renderCursor(GraphicsContext gc, Point offset) {
        long currentTime = System.currentTimeMillis();
        int animationFrame = (int) ((currentTime / MILLISECONDS_PER_FRAME) % WOBBLE_FRAME_COUNT);
        GraphicUtils.drawSprite(
                gc, TypeRegistry.JAVA.getSpriteSheet(),
                animationFrame, 0,
                SPRITE_SIZE * cursorPos.x + offset.x,
                SPRITE_SIZE * cursorPos.y + offset.y
        );
    }

    private void renderLevelName(GraphicsContext gc, Point offset) {
        String levelFileName = LEVEL_POSITION_FILENAME.get(cursorPos);

        if (levelFileName != null) {
            String cleanedName = levelFileName
                    .split("\\.")[0]
                    .split("/")[1]
                    .replace("_", " ");

            double textOffset = cleanedName.length() * FONT_WIDTH * LEVEL_NAME_SCALE;
            double mapOffset = levelSelectorMap.getWidth() * SPRITE_SIZE;

            GraphicUtils.drawText(
                    gc, cleanedName,
                    offset.x - (textOffset - mapOffset) / 2.0,
                    20,
                    LEVEL_NAME_SCALE
            );
        }
    }

    private void renderLevelNumber(GraphicsContext gc, Point offset) {
        for(Map.Entry<Point, String> entry : LEVEL_POSITION_FILENAME.entrySet()) {
            Point position = entry.getKey();
            String levelFileName = entry.getValue();
            String levelNumber = levelFileName.split("/")[1].split("_")[0];

            double textOffsetX = levelNumber.length() * FONT_WIDTH * LEVEL_NUMBER_SCALE;
            double textOffsetY = FONT_HEIGHT * LEVEL_NUMBER_SCALE - 2;
            double x = offset.x + SPRITE_SIZE * position.x + (SPRITE_SIZE - textOffsetX) / 2.0;
            double y = offset.y + SPRITE_SIZE * position.y + (SPRITE_SIZE - textOffsetY) / 2.0;

            GraphicUtils.drawText(
                    gc, levelNumber,
                    x, y,
                    LEVEL_NUMBER_SCALE
            );
        }
    }
}
