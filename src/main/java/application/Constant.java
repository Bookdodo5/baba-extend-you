package application;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Stores the constants for display and input configuration.
 */
public class Constant {
    public static final int TARGET_SCREEN_WIDTH = 1280;
    public static final int TARGET_SCREEN_HEIGHT = 720;

    public static final int INPUT_COOLDOWN_MILLIS = 150;
    public static final int MAX_ENTITY_LIMIT = 1024;

    public static final int SPRITE_SIZE = 32;
    public static final int MILLISECONDS_PER_FRAME = 150;
    public static final int WOBBLE_FRAME_COUNT = 3;

    public static final int FONT_WIDTH = 9;
    public static final int FONT_HEIGHT = 24;
    public static final int FONT_PER_ROW = 16;

    public static final int MILLISECONDS_PER_TITLE_CYCLE = 12000;
    public static final int WIN_DELAY_MILLIS = 3000;

    public static final List<Color> COLOR_LIST = List.of(
            Color.BLUEVIOLET,
            Color.BROWN,
            Color.CADETBLUE,
            Color.DARKBLUE,
            Color.DARKCYAN,
            Color.DARKGOLDENROD,
            Color.DARKGREY,
            Color.DARKOLIVEGREEN,
            Color.DARKRED,
            Color.DARKSALMON,
            Color.DARKSLATEBLUE,
            Color.DARKSLATEGRAY,
            Color.DEEPPINK,
            Color.DIMGRAY,
            Color.INDIGO,
            Color.LIGHTSTEELBLUE,
            Color.MAROON,
            Color.MEDIUMORCHID,
            Color.MEDIUMPURPLE,
            Color.MEDIUMSLATEBLUE,
            Color.MEDIUMVIOLETRED,
            Color.MIDNIGHTBLUE,
            Color.OLIVEDRAB,
            Color.ORANGERED,
            Color.ORANGE,
            Color.ORCHID,
            Color.PALEVIOLETRED,
            Color.PERU,
            Color.PINK,
            Color.PLUM,
            Color.PURPLE,
            Color.ROSYBROWN,
            Color.SADDLEBROWN,
            Color.SILVER,
            Color.SLATEGRAY,
            Color.STEELBLUE,
            Color.TEAL,
            Color.THISTLE
    );
}
