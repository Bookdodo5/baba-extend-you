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

/**
 * Utility class for image loading, color manipulation, scaling, and sprite drawing.
 */
public class ImageUtils {
    private static final Map<String, Image> COLOR_CACHE = new HashMap<>();
    private static final Map<Integer, Color> AVERAGE_COLOR_CACHE = new HashMap<>();

    /**
     * Computes the average color (excluding fully transparent pixels) of an image.
     * Results are cached by identity hash code.
     *
     * @param image the source image
     * @return the average color of the non-transparent pixels
     */
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

    /**
     * Returns a copy of the image with all non-transparent pixels replaced by the given color.
     * Results are cached by image identity and color.
     *
     * @param image the source image
     * @param color the replacement color
     * @return the tinted image
     */
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

    /**
     * Scales an image using nearest-neighbor interpolation (no blurring).
     *
     * @param source the source image
     * @param scale  the scale factor
     * @return the scaled image
     */
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

    /**
     * Loads an image from the given classpath resource path.
     *
     * @param path the classpath-relative path to the image resource
     * @return the loaded image
     * @throws NullPointerException if the resource is not found
     */
    public static Image getImage(String path) {
        InputStream inputStream = Objects.requireNonNull(
                ImageUtils.class.getResourceAsStream(path)
        );
        return new Image(inputStream);
    }

    /**
     * Draws a single sprite frame from a sprite sheet onto the graphics context.
     *
     * @param gc        the graphics context to draw on
     * @param image     the sprite sheet image
     * @param spriteCol the column index (animation frame) in the sprite sheet
     * @param spriteRow the row index in the sprite sheet
     * @param drawX     the x pixel coordinate to draw at
     * @param drawY     the y pixel coordinate to draw at
     */
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
