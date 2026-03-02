package utils;

import application.GameController;
import application.Audio;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;

import static application.Constant.*;

/**
 * Utility class for rendering, UI creation, and post-processing effects.
 */
public class GraphicUtils {

    public static final double TEXT_SCALE = 1.25;

    private static final Image FONT_SHEET = ImageUtils.getImage("/font/baba.png");
    private static final String CHARACTER_MAP = "ABCDEFGHIJKLMNOP" +
            "QRSTUVWXYZ012345" +
            "6789-.?!,':_><()" +
            "&+/^\"%";

    private static String getBaseButtonStyle() {
        Color theme = GameController.getInstance().getColorTheme();
        Color bgColor = theme.darker().darker();

        return String.format(
                "-fx-background-color: rgb(%d,%d,%d);" +
                        "-fx-border-color: rgb(%d,%d,%d);" +
                        "-fx-border-width: 3px;" +
                        "-fx-padding: 0px 20px;",
                (int) (bgColor.getRed() * 255),
                (int) (bgColor.getGreen() * 255),
                (int) (bgColor.getBlue() * 255),
                (int) (theme.getRed() * 255),
                (int) (theme.getGreen() * 255),
                (int) (theme.getBlue() * 255)
        );
    }

    private static String getHoverButtonStyle() {
        Color theme = GameController.getInstance().getColorTheme();

        return String.format(
                "-fx-background-color: rgb(%d,%d,%d);" +
                        "-fx-border-color: rgb(%d,%d,%d);" +
                        "-fx-border-width: 3px;" +
                        "-fx-padding: 0px 20px;" +
                        "-fx-cursor: hand;",
                (int) (theme.getRed() * 255),
                (int) (theme.getGreen() * 255),
                (int) (theme.getBlue() * 255),
                (int) (theme.getRed() * 255),
                (int) (theme.getGreen() * 255),
                (int) (theme.getBlue() * 255)
        );
    }

    /**
     * Creates an {@link HBox} rendering the given text string using the bitmap font,
     * with a custom scale and color.
     *
     * @param text  the text to render (case-insensitive)
     * @param scale the scale factor applied to each character
     * @param color the tint color applied to each character
     * @return an HBox containing the rendered text
     */
    public static HBox createTextNode(String text, double scale, Color color) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        for (char c : text.toUpperCase().toCharArray()) {
            int idx = CHARACTER_MAP.indexOf(c);
            if (idx == -1) {
                Pane space = new Pane();
                space.setPrefSize(FONT_WIDTH * scale, FONT_HEIGHT * scale);
                container.getChildren().add(space);
                continue;
            }

            int srcX = (idx % FONT_PER_ROW) * FONT_WIDTH;
            int srcY = (idx / FONT_PER_ROW) * FONT_HEIGHT + 2;

            PixelReader reader = FONT_SHEET.getPixelReader();
            WritableImage charImage = new WritableImage(reader, srcX, srcY, FONT_WIDTH, FONT_HEIGHT - 4);
            Image scaledImage = ImageUtils.scaleNearestNeighbor(charImage, scale);
            Image coloredImage = ImageUtils.applyColor(scaledImage, color);
            ImageView view = new ImageView(coloredImage);
            view.setSmooth(false);

            container.getChildren().add(view);
        }
        return container;
    }


    /**
     * Creates an {@link HBox} rendering the given text using the default scale and white color.
     *
     * @param text the text to render
     * @return an HBox containing the rendered text
     */
    public static HBox createTextNode(String text) {
        return createTextNode(text, TEXT_SCALE, Color.WHITE);
    }

    /**
     * Creates a styled {@link Button} with the given label, action, preferred width, and height.
     *
     * @param text      the label text
     * @param action    the action to run when clicked
     * @param prefWidth the preferred button width
     * @param prefHeight the preferred button height
     * @return the configured button
     */
    public static Button createButton(String text, Runnable action, int prefWidth, int prefHeight) {
        Button button = createButton(text, action);
        button.setPrefWidth(prefWidth);
        button.setPrefHeight(prefHeight);
        return button;
    }

    /**
     * Creates a styled {@link Button} with default height.
     *
     * @param text      the label text
     * @param action    the action to run when clicked
     * @param prefWidth the preferred button width
     * @return the configured button
     */
    public static Button createButton(String text, Runnable action, int prefWidth) {
        return createButton(text, action, prefWidth, (int) (FONT_HEIGHT * TEXT_SCALE));
    }

    /**
     * Creates a styled {@link Button} with auto-sized dimensions.
     *
     * @param text   the label text
     * @param action the action to run when clicked
     * @return the configured button
     */
    public static Button createButton(String text, Runnable action) {
        Button button = new Button();
        button.setGraphic(createTextNode(text));
        button.setText("");
        button.setStyle(getBaseButtonStyle());

        button.setOnAction((_) -> {
            Audio.playSfx("sound/SFX/confirm.wav");
            randomColorTheme();
            action.run();
        });

        button.setOnMouseEntered((_) -> {
            Audio.playSfx("sound/SFX/select.wav");
            button.setStyle(getHoverButtonStyle());
            button.setGraphic(createTextNode(text));
        });

        button.setOnMouseExited((_) -> {
            button.setStyle(getBaseButtonStyle());
            button.setGraphic(createTextNode(text));
        });
        return button;
    }

    /** Picks a random color from {@link application.Constant#COLOR_LIST} and applies it as the color theme. */
    public static void randomColorTheme() {
        Color randomColor = COLOR_LIST.get((int) Math.floor(Math.random() * COLOR_LIST.size()));
        GameController.getInstance().setColorTheme(randomColor);
    }

    /**
     * Creates an {@link ImageView} sprite indicator aligned to the left of a button container,
     * used to show the currently selected menu item.
     *
     * @param image       the sprite image to display
     * @param buttonWidth the width of the button column (used to compute horizontal offset)
     * @return the configured ImageView indicator
     */
    public static ImageView createButtonIndicator(Image image, int buttonWidth) {
        ImageView selectIndicator = new ImageView(image);
        selectIndicator.setViewport(new Rectangle2D(0, 0, SPRITE_SIZE, SPRITE_SIZE));
        selectIndicator.setTranslateX(-(buttonWidth + SPRITE_SIZE) / 2.0 - 5);
        StackPane.setAlignment(selectIndicator, Pos.TOP_CENTER);
        return selectIndicator;
    }

    /**
     * Animates and repositions the selection indicator to point at the currently selected button.
     *
     * @param selectIndicator      the indicator ImageView to update
     * @param currentSelectedIndex the index of the currently selected button
     * @param titleButtonHeight    the height of each button
     * @param spacing              the vertical spacing between buttons
     */
    public static void updateIndicatorPosition(ImageView selectIndicator, int currentSelectedIndex, int titleButtonHeight, int spacing) {
        if (selectIndicator != null) {
            // Animate wobble frames
            long currentTime = System.currentTimeMillis();
            int animationFrame = (int) ((currentTime / MILLISECONDS_PER_FRAME) % WOBBLE_FRAME_COUNT);
            selectIndicator.setViewport(new Rectangle2D(SPRITE_SIZE * animationFrame, 0, SPRITE_SIZE, SPRITE_SIZE));

            int yOffset = currentSelectedIndex * (titleButtonHeight + spacing);
            int buttonOffset = ((titleButtonHeight + spacing) - SPRITE_SIZE) / 2;
            selectIndicator.setTranslateY(yOffset + buttonOffset);
        }
    }

    /**
     * Applies a glow post-processing effect to the root pane.
     *
     * @param root the root StackPane to apply the effect to
     */
    public static void applyPostProcessing(StackPane root) {
        Glow glow = new Glow(0.7);
        root.setEffect(glow);
    }
}

