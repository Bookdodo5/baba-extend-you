package utils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static application.Constant.SPRITE_SIZE;

public class ImageUtils {
    private static final Map<String, Image> COLOR_CACHE = new HashMap<>();
    private static final Map<Integer, Color> AVERAGE_COLOR_CACHE = new HashMap<>();

    public static Color averageColor(Image image) {
        int imageHash = System.identityHashCode(image);
        Color cachedColor = AVERAGE_COLOR_CACHE.get(imageHash);
        if (cachedColor != null) {
            return cachedColor;
        }

        double redSum = 0;
        double greenSum = 0;
        double blueSum = 0;
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = image.getPixelReader().getColor(x, y);
                if(color.getOpacity() == 0) {
                    totalPixels--;
                    continue;
                }
                redSum += color.getRed();
                greenSum += color.getGreen();
                blueSum += color.getBlue();
            }
        }

        Color averageColor = Color.color(redSum / totalPixels, greenSum / totalPixels, blueSum / totalPixels);
        AVERAGE_COLOR_CACHE.put(imageHash, averageColor);
        return averageColor;
    }

    public static Image applyColor(Image image, Color color) {
        String cacheKey = System.identityHashCode(image) + "_" + color.toString();
        Image cachedImage = COLOR_CACHE.get(cacheKey);
        if (cachedImage != null) {
            return cachedImage;
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage coloredImage = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = image.getPixelReader().getColor(x, y);
                if(originalColor.getOpacity() == 0) {
                    coloredImage.getPixelWriter().setColor(x, y, originalColor);
                    continue;
                }
                coloredImage.getPixelWriter().setColor(x, y, color);
            }
        }

        COLOR_CACHE.put(cacheKey, coloredImage);
        return coloredImage;
    }

    public static Image scaleNearestNeighbor(Image source, double scale) {
        int scaledWidth = (int) (source.getWidth() * scale);
        int scaledHeight = (int) (source.getHeight() * scale);

        WritableImage scaledImage = new WritableImage(scaledWidth, scaledHeight);
        PixelReader reader = source.getPixelReader();

        for (int y = 0; y < scaledHeight; y++) {
            for (int x = 0; x < scaledWidth; x++) {
                int sourceX = (int) (x / scale);
                int sourceY = (int) (y / scale);
                scaledImage.getPixelWriter().setArgb(x, y, reader.getArgb(sourceX, sourceY));
            }
        }

        return scaledImage;
    }

    public static Image getImage(String path) {
        InputStream inputStream = Objects.requireNonNull(
                ImageUtils.class.getResourceAsStream(path)
        );
        return new Image(inputStream);
    }

    public static void drawSprite(GraphicsContext gc, Image image, int spriteCol, int spriteRow, int drawX, int drawY) {
        gc.drawImage(
                image,
                SPRITE_SIZE * spriteCol, SPRITE_SIZE * spriteRow,
                SPRITE_SIZE, SPRITE_SIZE,
                drawX,
                drawY,
                SPRITE_SIZE, SPRITE_SIZE
        );
    }
}
