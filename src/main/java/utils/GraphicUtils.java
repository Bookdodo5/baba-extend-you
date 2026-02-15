package utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Objects;

import static application.Constant.*;

public class GraphicUtils {

    private static final Image FONT_SHEET = new Image(
            Objects.requireNonNull(GraphicUtils.class.getResourceAsStream("/font/baba.png")),
            FONT_WIDTH * FONT_PER_ROW,
            FONT_HEIGHT * FONT_ROW_COUNT,
            false,
            false
    );

    private static final String CHARACTER_MAP = "ABCDEFGHIJKLMNOP" +
            "QRSTUVWXYZ012345" +
            "6789-.?!,':_><()" +
            "&+/^\"%";


    private static final String baseButtonStyle =
            "-fx-background-color: rgb(23,72,87);" +
                    "-fx-text-fill: white;" +
                    "-fx-border-color: rgb(42,163,173);" +
                    "-fx-border-width: 3px;" +
                    "-fx-padding: 0px 20px;"
            ;

    private static final String hoverButtonStyle =
            "-fx-background-color: rgb(42,163,173);" +
                    "-fx-text-fill: white;" +
                    "-fx-border-color: rgb(42,163,173);" +
                    "-fx-border-width: 3px;" +
                    "-fx-padding: 0px 20px;" +
                    "-fx-cursor: hand;";

    public static HBox createTextNode(String text, double scale) {
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
            ImageView view = new ImageView(scaledImage);
            view.setSmooth(false);

            container.getChildren().add(view);
        }
        return container;
    }

    public static HBox createTextNode(String text) {
        return createTextNode(text, TEXT_SCALE);
    }

    public static Button createButton(String text, Runnable action, int prefWidth) {
        Button button = createButton(text, action);
        button.setPrefWidth(prefWidth);
        button.setPrefHeight(FONT_HEIGHT * TEXT_SCALE);
        return button;
    }

    public static Button createButton(String text, Runnable action) {
        Button button = new Button();
        button.setGraphic(createTextNode(text, TEXT_SCALE));
        button.setText("");
        button.setStyle(baseButtonStyle);

        button.setOnAction((_) -> {
            // TODO (SOUND): play menu select sound
            action.run();
        });

        button.setOnMouseEntered((_) -> {
            // TODO (SOUND): play menu hover sound
            button.setStyle(hoverButtonStyle);
        });

        button.setOnMouseExited((_) -> {
            // TODO (SOUND): play menu hover sound
            button.setStyle(baseButtonStyle);
        });
        return button;
    }
}
